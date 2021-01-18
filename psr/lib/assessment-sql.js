const externalSources = require('./uuids/external-sources')
const questionUuids = require('./uuids/question-uuids')
const groupUuids = require('./uuids/group-uuids')
const questionGroupUuids = require('./uuids/question-group-uuids')
const answerSchemaUuids = require('./uuids/answer-schema-uuids')
const answerSchemaGroupUuids = require('./uuids/answer-schema-group-uuids')


class AssessmentSql {
  constructor(assessmentName, headers) {
    this.headers = headers

    this.answerSchemaGroups = []
    this.answerSchemas = []
    this.groups = []
    this.questions = []
    this.questionGroups = []
    this.topLevelGroup = this._createGrouping([assessmentName])
    this.currentGroup = this.topLevelGroup
    this.dependencies = []

    this.yes_no = ['radio', this.answerSchemaGroup(['drop-down', 'Yes|Y', 'No|N'])]

    this.previousQuestion = null
  }

  isGroup(record) {
    const cleaned = record.filter(f => f)
    return (record[this.headers.TITLE] && cleaned.length === 1)
  }

  addGrouping(record) {
    const group = this._createGrouping(record)
    this._addGroupQuestion(group.group_uuid, 'group', this.topLevelGroup.group_uuid)
    this.currentGroup = group
    return group
  }

  _createGrouping(record) {
    const heading = record[this.headers.TITLE]
    const groupCode = heading.replace(/[ ',\\.\\(\\)\\?\\/]+/g, '_').toLowerCase()
    const group = {
      group_uuid: groupUuids(groupCode, groupCode),
      group_code: groupCode,
      heading: heading,
      group_start: '2020-11-30 14:50:00'
    }
    this.groups.push(group)
    return group
  }

  _addGroupQuestion(content_uuid, content_type, group_uuid, validation) {
    const questionGroup = {
      question_group_uuid: questionGroupUuids(content_uuid),
      content_uuid: content_uuid,
      content_type: content_type,
      group_uuid: group_uuid,
      display_order: this.questionGroups.filter(qg => qg.group_uuid === group_uuid).length + 1,
      mandatory: true,
      validation: JSON.stringify(validation)
    }
    this.questionGroups.push(questionGroup)
  }

  addQuestion(record) {
    const question = this._createQuestion(record)
    this._addGroupQuestion(
      question.question_schema_uuid,
      'question',
      this.currentGroup.group_uuid,
      this.compileValidation(record)
    )
    this.previousQuestion = question
    return question
  }

  _createQuestion(record) {
    const question_title = record[this.headers.TITLE].replace(/[ ',\\.\\(\\)\\?\\/]+/g, '_').toLowerCase()
    const question_code = record[this.headers.REF]
    const question_text = record[this.headers.QUESTION].replace(/'/g, "''")
    const [answer_type, answer_schema_group_uuid] = this.answerType(record[this.headers.ANSWER_TYPE])
    const oasys_question_code = record[this.headers.OASYS_REF] || null
    const question = {
      question_schema_uuid: questionUuids(question_code, question_title),
      question_code: question_code,
      oasys_question_code: oasys_question_code,
      answer_type: answer_type,
      answer_schema_group_uuid: answer_schema_group_uuid,
      question_text: question_text,
      question_start: '2020-11-30 14:50:00',
      external_source: externalSources(question_code)
    }
    this.questions.push(question)

    if (question_text === 'Give details') { // HOLD YOUR NOSE, THIS IS STINKY
      this.dependencies.push({
        subject_question_uuid: question.question_schema_uuid,
        trigger_question_uuid: this.previousQuestion.question_schema_uuid,
        trigger_answer_value: 'Y',
        dependency_start: '2020-11-30 14:50:00'
      })
    }

    return question
  }

  answerType(answerField) {
    const lines = answerField.split('\n')
    if (lines.length === 1 && lines[0] === 'Y/N')
      return this.yes_no

    if (lines.length !== 1) {
      const type = lines[0].toLowerCase()
      if (type.match(/drop-?down/))
        return ['dropdown', this.answerSchemaGroup(lines)]
      if (type.match(/radio/))
        return ['radio', this.answerSchemaGroup(lines)]
    }

    if (answerField.match(/date/i))
      return ['date', null]
    return ['freetext', null]
  }

  answerSchemaGroup(lines) {
    lines = lines.slice(1).map(line => line.replace(/(\r|\n)/, '').split('|')).map(([a, v]) => v ? [a, v] : [a, a.toLowerCase()])

    const name = lines
      .map(([a, v]) => a)
      .join('-')
      .replace(/[ ',\\.\\(\\)\\?\\/]+/g, '')
      .toLowerCase()
    const existing = this.answerSchemaGroups
      .find(a => a.answer_schema_group_code === name)
    if (existing) return existing.answer_schema_group_uuid

    const answerGroup = {
      answer_schema_group_uuid: answerSchemaGroupUuids(name),
      answer_schema_group_code: name,
      group_start: '2020-11-30 14:50:00',
      group_end: null
    }

    this.answerSchemaGroups.push(answerGroup)

    for (const [text, value] of lines) {
      if (!text) continue
      const code = text.replace(/[ ',\\.\\(\\)\\?\\/]+/g, '_').toLowerCase()
      const answerSchema = {
        answer_schema_uuid: answerSchemaUuids(code),
        answer_schema_code: code,
        answer_schema_group_uuid: answerGroup.answer_schema_group_uuid,
        answer_start: '2020-11-30 14:50:00',
        answer_end: null,
        value: value,
        text: text
      }
      this.answerSchemas.push(answerSchema)
    }
    return answerGroup.answer_schema_group_uuid
    return answerGroup.answer_schema_group_uuid
  }

  compileValidation(record) {
    return { mandatory: { errorMessage: record[this.headers.ERROR_MESSAGE], errorSummary: record[this.headers.ERROR_SUMMARY].replace(/(^There is a problem\n\n)/mg, '')}}
  }

  toSql() {
    console.log(this.answerSchemaGroupSql())
    console.log(this.answerSchemaSql())
    console.log(this.groupingSql())
    console.log(this.questionsSql())
    console.log(this.questionGroupSql())
    console.log(this.dependenciesSql())
  }

  ///////////////////////////
  static insertSql(table, fields) {
    return `INSERT INTO ${table} (${fields.join(', ')})\nVALUES `
  }

  static valueSql(fields, obj) {
    const values = fields
      .map(field => obj[field] ? obj[field] : null)
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
      ['question_group_uuid', 'content_uuid', 'content_type', 'group_uuid', 'display_order', 'mandatory', 'validation'],
      nonEmptyQuestionGroups
    )
  }

  dependenciesSql() {
    return AssessmentSql.tableSql(
      'question_dependency',
      ['subject_question_uuid', 'trigger_question_uuid', 'trigger_answer_value', 'dependency_start'],
      this.dependencies
    )
  }
}

module.exports = (name, headers) => new AssessmentSql(name, headers)
