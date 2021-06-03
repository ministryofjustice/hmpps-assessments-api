const externalSources = require('./uuids/external-sources')
const questionUuids = require('./uuids/question-uuids')
const groupUuids = require('./uuids/group-uuids')
const questionGroupUuids = require('./uuids/question-group-uuids')
const answerSchemaUuids = require('./uuids/answer-schema-uuids')
const answerSchemaGroupUuids = require('./uuids/answer-schema-group-uuids')
const oasysMappingUuids = require('./uuids/oasys-mapping-uuids')
const oasysQuestions = require('./oasys/oasys-questions')

class AssessmentSql {
  constructor(assessmentName, headers, incremental) {
    this.headers = headers
    this.incremental = incremental

    this.answerSchemaGroups = []
    this.answerSchemas = []
    this.groups = []
    this.questions = []
    this.oasysMapping = []
    this.questionGroups = []
    this.assessmentGroup = this._createGrouping([assessmentName])
    this.currentGroup = this.assessmentGroup
    this.dependencies = []
    this.questionsWithFilteredReferenceData = []
    this.fixedOasysCodeLookup = []
    this.filteredReferenceDataMappings = []

    this.oasysQuestions = oasysQuestions()
    this.yes_no = ['radio', this.answerSchemaGroup(['radios:', 'Yes|YES', 'No|NO'])]
  }

  addFilteredReferenceDataTargets() {
    const hasReferenceDataTarget = ({question_schema_uuid}) => this.questionsWithFilteredReferenceData.filter(([uuid]) => uuid === question_schema_uuid).length > 0
    const withTargets = this.questions.filter(q => hasReferenceDataTarget(q))

    console.warn(`Adding reference data targets for ${withTargets.length} item(s)`)

    this.filteredReferenceDataMappings = withTargets.map(question => {
          const [question_schema_uuid, target_fields_string] = this.questionsWithFilteredReferenceData.filter(([uuid]) => uuid === question.question_schema_uuid).shift()
          return target_fields_string
              .split(',')
              .map(s => s.trim())
              .filter(s => s.match(/^\w+\/\w+\??$/g))
              .map(s => {
                const [_, fixed_field, is_optional] = s.match(/^(\w+\/\w+)(\?)?$/)

                const matches = this.fixedOasysCodeLookup.filter(([oasys_fixed_field]) =>  oasys_fixed_field === fixed_field)

                if (matches.length < 1) {
                  console.warn(`No matching target found for question "${question.question_text}"`)
                } else if (matches.length > 1) {
                  console.warn(`Ambiguous match for question "${question.question_text}" - ${matches.length} results found for reference data target`)
                } else {
                  const [[_, parent_question_schema_uuid]] = matches
                  console.warn(`Found reference data target UUID for "${question.question_text}" - "${parent_question_schema_uuid}"`)
                  return {
                    question_schema_uuid,
                    parent_question_schema_uuid,
                    is_required: !is_optional
                  }
                }

                return null
              })
              .filter(m => m)
        })
        .flat()
  }

  addDependencyUuids() {
    this.dependencies.map(dependency => {
      if (dependency.subject_question_code) {
        const question = this.questions.filter(question => question.question_code === dependency.subject_question_code)
        if (question.length === 0)
          return console.warn(`Could not find dependent question ${dependency.subject_question_code}`)

        dependency.subject_question_uuid = question[0].question_schema_uuid
      }
      delete dependency.subject_question_code
      return dependency
    })
    this.dependencies = this.dependencies.filter(dependency => dependency.subject_question_uuid)
  }

  isTopLevelGroup(record) {
    // if ref is S1, S3, S12 etc this is a top level group
    const hasHeadingRef = record[this.headers.REF].match(/^S[1-9]+$/)
    return hasHeadingRef
  }

  isGroup(record) {
    return (record[this.headers.TITLE] && record.filter(f => f).length <= 2)
  }

  addGrouping(record) {
    const group = this._createGrouping(record)
    const isTopLevel = this.isTopLevelGroup(record)
    const parent = isTopLevel ? this.assessmentGroup : this.topLevelGroup

    this._addGroupQuestion(group.group_uuid, 'group', parent.group_uuid)
    this.currentGroup = group
    if (isTopLevel) this.topLevelGroup = group
    return group
  }

  _createGrouping(record) {
    const heading = record[this.headers.TITLE].replace(/'/g, "''")
    const groupCode = heading.replace(/[ '\-,\\.\\(\\)\\?\\/]+/g, '_').toLowerCase()
    const [uuid, existing] = groupUuids(groupCode, groupCode)
    const group = {
      group_uuid: uuid,
      group_code: groupCode,
      heading: heading,
      group_start: '2020-11-30 14:50:00'
    }
    if (this.shouldAdd(existing))
      this.groups.push(group)
    return group
  }

  _addGroupQuestion(content_uuid, content_type, group_uuid, validation, read_only = false) {
    const [uuid, existing] = questionGroupUuids(content_uuid, group_uuid)
    const questionGroup = {
      question_group_uuid: uuid,
      content_uuid: content_uuid,
      content_type: content_type,
      group_uuid: group_uuid,
      display_order: this.questionGroups.filter(qg => qg.group_uuid === group_uuid).length + 1,
      mandatory: true,
      validation: validation,
      read_only: read_only
    }
    if (this.shouldAdd(existing))
      this.questionGroups.push(questionGroup)
  }

  addQuestion(record) {
    const question = this._createQuestion(record)
    if (!question)
      return

    this._addGroupQuestion(
      question.question_schema_uuid,
      'question',
      this.currentGroup.group_uuid,
      this.compileValidation(record),
      question.read_only
    )
    return question
  }

  _createQuestion(record) {
    const oasys_question_code = record[this.headers.OASYS_REF] || null
    const oasys_fixed_field = record[this.headers.OASYS_FIXED] || null
    const oasys_question = this.oasysQuestions.lookup(oasys_question_code, oasys_fixed_field)

    const question_title = record[this.headers.TITLE].replace(/[ ',\\.\\(\\)\\?\\/]+/g, '_').toLowerCase()
    const question_code = record[this.headers.REF]

    if (!question_code)
      return

    const question_text = record[this.headers.QUESTION].replace(/'/g, "''").replace(/\r\n/g, ' ').trim()
    const question_help_text = record[this.headers.HINT_TEXT].replace(/'/g, "''").replace(/\r\n/g, ' ').replace(/\[.*\] *\n*/g, '')
    const [answer_type, answer_schema_group_uuid, read_only] = this.answerType(record[this.headers.ANSWER_TYPE], oasys_question)
    const [question_schema_uuid, existing] = questionUuids(question_code, answer_type, question_title)

    const business_logic = record[this.headers.LOGIC]
    let reference_data_category = record[this.headers.REFERENCE_DATA_CATEGORY]
    const reference_data_target = record[this.headers.REFERENCE_DATA_TARGET]

    const isDynamicType = (answer_field) => {
      const type = answer_field.split('\n').shift().toLowerCase()
      return type.match(/dynamic-drop-?down/) || type.match(/dynamic-radio/)
    }

    if (isDynamicType(record[this.headers.ANSWER_TYPE])) {
      this.questionsWithFilteredReferenceData.push([question_schema_uuid, reference_data_target])
      reference_data_category = 'FILTERED_REFERENCE_DATA'
    }

    if (oasys_fixed_field)
      this.fixedOasysCodeLookup.push([oasys_fixed_field, question_schema_uuid])

    const question = {
      question_schema_uuid: question_schema_uuid,
      question_code: question_code,
      oasys_question_code: oasys_question_code,
      answer_type: answer_type,
      answer_schema_group_uuid: answer_schema_group_uuid,
      question_text: question_text,
      question_help_text: question_help_text,
      question_start: '2020-11-30 14:50:00',
      external_source: externalSources(question_code),
      read_only: (question_code.substring(0,2) === 'ui' ? true : read_only),
      reference_data_category: reference_data_category,
    }
    if (this.shouldAdd(existing))
      this.questions.push(question)

    if (oasys_question) {
      const mapping = {
        mapping_uuid: oasysMappingUuids(question.question_schema_uuid, oasys_question)[0],
        question_schema_uuid: question.question_schema_uuid,
        ref_section_code: oasys_question.ref_section_code,
        logical_page: oasys_question.logicalpage,
        ref_question_code: oasys_question.ref_question_code,
        fixed_field: oasys_question.fixed_field === true
      }
      this.oasysMapping.push(mapping)
    }

    if (business_logic && business_logic.toLowerCase() !== 'none' && business_logic.toLowerCase() !== 'TBC') {
      // for each line in the business logic, get the answer value and target
      // this assumes the logic in the spreadsheet is in the format:
      // Some problems > 76.1
      // Significant problems > 76.2 out-of-line
      const logicLines = business_logic.split('\n').map(line => {
        const logic = line.split(' > ')
        let target = logic[1]
        if (logic[1]) {
          const checkForInline = logic[1].split(' ')
          let display_inline = true
          if (checkForInline[1] && checkForInline[1].indexOf('out-of-line' !== -1)) {
            target = checkForInline[0]
            display_inline = false
          }

          return { value: logic[0], target, display_inline }
        } else {
          return []
        }
      })

      // write an entry to our list of dependencies. This needs to have UUIDs added for each question code
      // once all questions have been processed.
      logicLines.forEach(dependency => {
        if (dependency.target) {
          this.dependencies.push({
            subject_question_code: dependency.target.trim(),
            trigger_question_uuid: question.question_schema_uuid,
            trigger_answer_value: dependency.value.trim().replace(/'/g, "''"),
            dependency_start: '2020-11-30 14:50:00',
            display_inline: dependency.display_inline
          })
        }
      })
    }

    return question
  }

  answerType(answerField, oasysQuestion) {
    const input = this.inputType(answerField, oasysQuestion)
    const read_only = answerField.match(/read[ -]?only/i) !== null
    return [...input, read_only]
  }

  inputType(answerField, oasysQuestion) {
    let lines = answerField.split('\n')

    if (lines.length === 1 && lines[0] === 'Y/N')
      return this.yes_no

    if (oasysQuestion?.answers)
      lines = [lines[0], ...oasysQuestion.answers.slice(1)]

    if (lines.length !== 1) {
      const type = lines[0].toLowerCase()
      if (type.match(/drop-?down/))
        return ['dropdown', this.answerSchemaGroup(lines)]
      if (type.match(/radio/))
        return ['radio', this.answerSchemaGroup(lines)]
      if (type.match(/checkbox/))
        return ['checkbox', this.answerSchemaGroup(lines)]
      if (oasysQuestion?.answers) {
        return [oasysQuestion.answers[0], this.answerSchemaGroup(oasysQuestion.answers)]
      }
    }

    if (answerField.match(/dynamic-radio/i))
      return ['radio', null]
    if (answerField.match(/dynamic-drop-?down/i))
      return ['dropdown', null]

    if (answerField.match(/no ?input/i))
      return ['noinput', null]
    if (answerField.match(/text ?area/i))
      return ['textarea', null]
    if (answerField.match(/^date/i))
      return ['date', null]
    if (answerField.match(/^presentation:/i))
      return [answerField, null]
    if (answerField.match(/^numeric/i))
      return ['numeric', null]
    if (answerField.match(/^table\:/))
      return [answerField, null]
    return ['freetext', null]
  }

  answerSchemaGroup(lines) {
    const newlines = lines.slice(1).map(line => line.replace(/(\r|\n)/, '').split('|')).map(([a, v]) => v ? [a, v] : [a, a.toLowerCase()])

    if (lines[0].indexOf('drop-down') !== -1 || lines[0].indexOf('dropdown') !== -1) {
      newlines.unshift(['Select an answer', null]);
    }

    const name = newlines
      .map(([a, v]) => a)
      .join('-')
      .replace(/[ ',\\.\\(\\)\\?\\/]+/g, '')
      .toLowerCase()
    const existingGroup = this.answerSchemaGroups
      .find(a => a.answer_schema_group_code === name)
    if (existingGroup) return existingGroup.answer_schema_group_uuid

    const [uuid, existing] = answerSchemaGroupUuids(name)
    const answerGroup = {
      answer_schema_group_uuid: uuid,
      answer_schema_group_code: name || uuid,
      group_start: '2020-11-30 14:50:00',
      group_end: null
    }

    if (this.shouldAdd(existing))
      this.answerSchemaGroups.push(answerGroup)

    for (const [text, value] of newlines) {
      if (!text) continue
      const code = text.replace(/[ ',\\.\\(\\)\\?\\/]+/g, '_').toLowerCase()
      const [uuid, existing] = answerSchemaUuids(code, answerGroup.answer_schema_group_code)
      const answerSchema = {
        answer_schema_uuid: uuid,
        answer_schema_code: code,
        answer_schema_group_uuid: answerGroup.answer_schema_group_uuid,
        answer_start: '2020-11-30 14:50:00',
        answer_end: null,
        value: value ? value.replace(/'+/g, '\'\'') : null,
        text: text ? text.replace(/'+/g, '\'\'') : null,
      }
      if (this.shouldAdd(existing))
        this.answerSchemas.push(answerSchema)
    }
    return answerGroup.answer_schema_group_uuid
  }

  compileValidation(record) {
    const errorMessage = record[this.headers.ERROR_MESSAGE]
    if (errorMessage === '' || errorMessage.startsWith('N/A')) return null

    return JSON.stringify({
      mandatory: {
        errorMessage: errorMessage,
        errorSummary: record[this.headers.ERROR_SUMMARY].replace(/(^There is a problem\n\n)/mg, '')
      }
    }).replace(/'/g, "''")
  }

  toSql() {
    console.log(this.answerSchemaGroupSql())
    console.log(this.answerSchemaSql())
    console.log(this.groupingSql())
    console.log(this.questionsSql())
    console.log(this.questionGroupSql())
    console.log(this.dependenciesSql())
    console.log(this.mappingSql())
    console.log(this.filteredReferenceDataMappingsSql())
  }

  shouldAdd(existing) {
    if (!existing) return true
    return !this.incremental
  }

  ///////////////////////////
  static insertSql(table, fields) {
    return `INSERT INTO ${table} (${fields.join(', ')})\nVALUES `
  }

  static valueSql(fields, obj) {
    const values = fields
      .map(field => obj[field] || obj[field] === false ? obj[field] : null)
      .map(value => (typeof value === 'string') ? `'${value}'` : value)
      .map(value => (value !== null) ? value : 'null')
      .join(', ')
    return `(${values})`
  }

  static tableSql(table, fields, data) {
    if (data.length === 0) return ''
    const insert = AssessmentSql.insertSql(table, fields)
    const values = data.map(row => AssessmentSql.valueSql(fields, row)).join(',\n')
    return `${insert}${values}\nON CONFLICT DO NOTHING;\n\n`
  }

  answerSchemaGroupSql() {
    return AssessmentSql.tableSql(
      'answer_schema_group',
      ['answer_schema_group_uuid', 'answer_schema_group_code', 'group_start', 'group_end' ],
      this.answerSchemaGroups
    )
  }

  answerSchemaSql() {
    return AssessmentSql.tableSql(
      'answer_schema',
      ['answer_schema_uuid', 'answer_schema_code', 'answer_schema_group_uuid', 'answer_start', 'answer_end', 'value', 'text'],
      this.answerSchemas
    )
  }

  groupingSql() {
    const nonEmptyGroups = this.groups.filter(g =>
      (this.questionGroups.filter(qg => qg.group_uuid === g.group_uuid).length !== 0)
    )

    return AssessmentSql.tableSql(
      'grouping',
      ['group_uuid', 'group_code', 'heading', 'subheading', 'help_text', 'group_start', 'group_end'],
      nonEmptyGroups
    )
  }

  questionsSql() {
    return AssessmentSql.tableSql(
      'question_schema',
      ['question_schema_uuid', 'question_code', 'oasys_question_code', 'question_start', 'question_end', 'answer_type', 'answer_schema_group_uuid', 'question_text', 'question_help_text', 'external_source', 'reference_data_category'],
      this.questions
    )
  }

  questionGroupSql() {
    const emptyGroups = this.groups
      .map(g => g.group_uuid)
      .filter(group_uuid => (this.questionGroups.filter(qg => qg.group_uuid === group_uuid).length === 0))

    const nonEmptyQuestionGroups = this.questionGroups.filter(qg => !emptyGroups.includes(qg.content_uuid))

    return AssessmentSql.tableSql(
      'question_group',
      ['question_group_uuid', 'content_uuid', 'content_type', 'group_uuid', 'display_order', 'mandatory', 'validation', 'read_only'],
      nonEmptyQuestionGroups
    )
  }

  dependenciesSql() {
    return AssessmentSql.tableSql(
      'question_dependency',
      ['subject_question_uuid', 'trigger_question_uuid', 'trigger_answer_value', 'dependency_start', 'display_inline'],
      this.dependencies
    )
  }

  mappingSql() {
    return AssessmentSql.tableSql(
      'oasys_question_mapping',
      ['mapping_uuid', 'question_schema_uuid', 'ref_section_code', 'logical_page', 'ref_question_code', 'fixed_field'],
      this.oasysMapping
    )
  }

  filteredReferenceDataMappingsSql() {
    return AssessmentSql.tableSql(
        'oasys_reference_data_target_mapping',
        ['question_schema_uuid', 'parent_question_schema_uuid', 'is_required'],
        this.filteredReferenceDataMappings
    )
  }
}

module.exports = (name, headers, incremental) => new AssessmentSql(name, headers, incremental)
