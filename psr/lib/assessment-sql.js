const externalSources = require('./uuids/external-sources')
const questionUuids = require('./uuids/question-uuids')
const groupUuids = require('./uuids/group-uuids')
const questionGroupUuids = require('./uuids/question-group-uuids')
const answerSchemaUuids = require('./uuids/answer-schema-uuids')
const answerSchemaGroupUuids = require('./uuids/answer-schema-group-uuids')
const oasysMappingUuids = require('./uuids/oasys-mapping-uuids')
const oasysQuestions = require('./oasys/oasys-questions')

class AssessmentSql {
  constructor(assessmentName, headers) {
    this.headers = headers

    this.answerSchemaGroups = []
    this.answerSchemas = []
    this.groups = []
    this.questions = []
    this.oasysMapping = []
    this.questionGroups = []
    this.assessmentGroup = this._createGrouping([assessmentName])
    this.currentGroup = this.assessmentGroup
    this.dependencies = []

    this.oasysQuestions = oasysQuestions()
    this.yes_no = ['radio', this.answerSchemaGroup(['radios:', 'Yes|YES', 'No|NO'])]
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
    const group = {
      group_uuid: groupUuids(groupCode, groupCode),
      group_code: groupCode,
      heading: heading,
      group_start: '2020-11-30 14:50:00'
    }
    // duplicate section names :(
    const existing = this.groups.filter(g => g.group_uuid === group.group_uuid)
    if (existing.length) {
      record[this.headers.TITLE] = `${heading}~`
      return this._createGrouping(record)
    }

    this.groups.push(group)
    return group
  }

  _addGroupQuestion(content_uuid, content_type, group_uuid, validation = null, read_only = false) {
    const questionGroup = {
      question_group_uuid: questionGroupUuids(content_uuid),
      content_uuid: content_uuid,
      content_type: content_type,
      group_uuid: group_uuid,
      display_order: this.questionGroups.filter(qg => qg.group_uuid === group_uuid).length + 1,
      mandatory: true,
      validation: validation,
      read_only: read_only
    }
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

    const question_text = record[this.headers.QUESTION].replace(/'/g, "''").replace(/\r\n/g, ' ')
    const question_help_text = record[this.headers.HINT_TEXT].replace(/'/g, "''").replace(/\r\n/g, ' ').replace(/\[.*\] *\n*/g, '')
    const [answer_type, answer_schema_group_uuid, read_only] = this.answerType(record[this.headers.ANSWER_TYPE], oasys_question)

    const business_logic = record[this.headers.LOGIC]
    const question = {
      question_schema_uuid: questionUuids(question_code, answer_type, question_title),
      question_code: question_code,
      oasys_question_code: oasys_question_code,
      answer_type: answer_type,
      answer_schema_group_uuid: answer_schema_group_uuid,
      question_text: question_text,
      question_help_text: question_help_text,
      question_start: '2020-11-30 14:50:00',
      external_source: externalSources(question_code),
      read_only: (question_code.substring(0,2) === 'ui' ? true : read_only)
    }
    this.questions.push(question)

    if (oasys_question) {
      const mapping = {
        mapping_uuid: oasysMappingUuids(question.question_schema_uuid, oasys_question),
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

    if (answerField.match(/text ?area/i))
      return ['textarea', null]
    if (answerField.match(/^date/i))
      return ['date', null]
    if (answerField.match(/^presentation:/i))
      return [answerField, null]
    if (answerField.match(/^numeric/i))
      return ['numeric', null]
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
    const existing = this.answerSchemaGroups
      .find(a => a.answer_schema_group_code === name)
    if (existing) return existing.answer_schema_group_uuid

    const answerGroup = {
      answer_schema_group_uuid: answerSchemaGroupUuids(name),
      answer_schema_group_code: name ||  answerSchemaGroupUuids(name),
      group_start: '2020-11-30 14:50:00',
      group_end: null
    }

    this.answerSchemaGroups.push(answerGroup)

    for (const [text, value] of newlines) {
      if (!text) continue
      const code = text.replace(/[ ',\\.\\(\\)\\?\\/]+/g, '_').toLowerCase()
      const answerSchema = {
        answer_schema_uuid: answerSchemaUuids(code, answerGroup.answer_schema_group_code),
        answer_schema_code: code,
        answer_schema_group_uuid: answerGroup.answer_schema_group_uuid,
        answer_start: '2020-11-30 14:50:00',
        answer_end: null,
        value: value ? value.replace(/'+/g, '\'\'') : null,
        text: text ? text.replace(/'+/g, '\'\'') : null,
      }
      this.answerSchemas.push(answerSchema)
    }
    return answerGroup.answer_schema_group_uuid
  }

  compileValidation(record) {
    const errorMessage = record[this.headers.ERROR_MESSAGE]
    if (errorMessage.startsWith('N/A'))
      return null

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
    const insert = AssessmentSql.insertSql(table, fields)
    const values = data.map(row => AssessmentSql.valueSql(fields, row)).join(',\n    ')
    return `${insert}${values};\n\n`
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
      ['question_schema_uuid', 'question_code', 'oasys_question_code', 'question_start', 'question_end', 'answer_type', 'answer_schema_group_uuid', 'question_text', 'question_help_text', 'external_source'],
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
}

module.exports = (name, headers) => new AssessmentSql(name, headers)
