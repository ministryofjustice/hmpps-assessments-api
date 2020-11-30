const parse = require('csv-parse/lib/sync')
const fs = require('fs')
const { v4: uuid } = require('uuid')
const snakeCase = require('lodash.snakecase')

const input = fs.readFileSync('short-form-psr.csv')

const all_records = parse(input, {
  columns: false,
  relax_column_count: true,
  skip_empty_lines: true
})

const records = all_records.filter(record => record.join('')) // remove lines with no content

const groups = []
const questions = []
const questionGroups = []
const psr = addGrouping(['Pre-Sentence Report'])

let currentGroup = null

for (let index = 1; index !== records.length; ++index) {
  const record = records[index];

  if (isGroup(record)) {
    currentGroup = addGrouping(record)
    addQuestionGroup(currentGroup.group_uuid, 'group', psr.group_uuid)
    continue
  }

  if (!currentGroup)
    continue

  const question = addQuestion(record)
  addQuestionGroup(question.question_schema_uuid, 'question', currentGroup.group_uuid)
}

console.log(groupingSql())
console.log(questionsSql())
console.log(questionGroupSql())

function isGroup(record) {
  const cleaned = record.filter(f => f)
  return (record[0] && cleaned.length == 1)
}

function addGrouping(record) {
  const heading = record[0]
  const group = {
    group_id: groups.length + 100,
    group_uuid: uuid(),
    group_code: snakeCase(heading),
    heading: heading,
    group_start: '2020-11-30 14:50:00'
  }
  groups.push(group)
  return group
}

function addQuestion(record) {
  const title = record[0]
  const question_text = record[9].replace(/'/g, "''")
  const answer_type = 'freetext' // record[11]
  const oasys_question_code = record[17] || null
  const question = {
    question_schema_id: questions.length + 100,
    question_schema_uuid: uuid(),
    question_code: snakeCase(title),
    oasys_question_code: oasys_question_code,
    answer_type: answer_type,
    question_text: question_text,
    question_start: '2020-11-30 14:50:00'
  }
  questions.push(question)
  return question
}

function addQuestionGroup(content_uuid, content_type, group_uuid) {
  const questionGroup = {
    question_group_id: questionGroups.length + 101,
    question_group_uuid: uuid(),
    content_uuid: content_uuid,
    content_type: content_type,
    group_uuid: group_uuid,
    display_order: questionGroups.filter(qg => qg.group_uuid === group_uuid).length + 1,
    mandatory: 'yes',
    validation: null
  }
  questionGroups.push(questionGroup)
}

function insertSql(table, fields) {
  return `INSERT INTO ${table} (${fields.join(', ')})\nVALUES `
}

function valueSql(fields, obj) {
  const values = fields
	.map(field => obj[field] ? obj[field] : null)
        .map(value => (typeof value === 'string') ? `'${value}'` : value)
        .map(value => (value !== null) ? value : 'null')
        .join(', ')
  return `(${values})`
}

function tableSql(table, fields, data) {
  const insert = insertSql(table, fields)
  const values = data.map(row => valueSql(fields, row)).join(',\n    ')
  return `${insert}${values};\n\n`
}

function groupingSql() {
  return tableSql(
    'grouping',
    ['group_id', 'group_uuid', 'group_code', 'heading', 'subheading', 'help_text', 'group_start', 'group_end'],
    groups
  )
}

function questionsSql() {
  return tableSql(
    'question_schema',
    ['question_schema_id', 'question_schema_uuid', 'question_code', 'oasys_question_code', 'question_start', 'question_end', 'answer_type', 'answer_schema_group_uuid', 'question_text', 'question_help_text'],
    questions
  )
}

function questionGroupSql() {
  return tableSql(
    'question_group',
    ['question_group_id', 'question_group_uuid', 'content_uuid', 'content_type', 'group_uuid', 'display_order', 'mandatory', 'validation'],
    questionGroups
  )
}
