const parse = require('csv-parse/lib/sync')
const fs = require('fs')
const { v4: uuid } = require('uuid')

if (process.argv.length !== 3) {
  return console.error("Usage: node assessment-generator <csv-file>")
}

const { headers, records } = loadSpreadsheet(process.argv[2])

const externalSources = require('./lib/external-sources')
const questionUuids = require('./lib/question-uuids')
const groupUuids = require('./lib/group-uuids')
const questionGroupUuids = require('./lib/question-group-uuids')
const answerSchemaUuids = require('./lib/answer-schema-uuids')

const answerSchemaGroups = []
const answerSchemas = []
const groups = []
const questions = []
const questionGroups = []
const assessment = addGrouping(['Pre-Sentence Assessment'])
const dependencies = []

const yes_no = ['radio', answerSchemaGroup(['drop-down', 'Yes|Y', 'No|N'])]

let currentGroup = null
let previousQuestion = null

for (const record of records) {
  if (isGroup(record)) {
    currentGroup = addGrouping(record)
    addGroupQuestion(currentGroup.group_uuid, 'group', assessment.group_uuid)
    continue
  }

  if (!currentGroup)
    continue

  const question = addQuestion(record, externalSources)
  addGroupQuestion(question.question_schema_uuid, 'question', currentGroup.group_uuid, compileValidation(record))
  previousQuestion = question
}

console.log(answerSchemaGroupSql())
console.log(answerSchemaSql())
console.log(groupingSql())
console.log(questionsSql())
console.log(questionGroupSql())
console.log(dependenciesSql())

function compileValidation(record){
  return { mandatory: { errorMessage: record[headers.ERROR_MESSAGE], errorSummary: record[headers.ERROR_SUMMARY].replace(/(^There is a problem\n\n)/mg, '')}}
}
function isGroup(record) {
  const cleaned = record.filter(f => f)
  return (record[headers.TITLE] && cleaned.length == 1)
}

function addGrouping(record) {
  const heading = record[headers.TITLE]
  const groupCode = heading.replace(/[ ',\\.\\(\\)\\?\\/]+/g, '_').toLowerCase()
  const group = {
    group_uuid: groupUuids(groupCode, groupCode),
    group_code: groupCode,
    heading: heading,
    group_start: '2020-11-30 14:50:00'
  }
  groups.push(group)
  return group
}

function addQuestion(record, externalSources) {
  const question_title = record[headers.TITLE].replace(/[ ',\\.\\(\\)\\?\\/]+/g, '_').toLowerCase()
  const question_code = record[headers.REF]
  const question_text = record[headers.QUESTION].replace(/'/g, "''")
  const [answer_type, answer_schema_group_uuid] = answerType(record[headers.ANSWER_TYPE])
  const oasys_question_code = record[headers.OASYS_REF] || null
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
  questions.push(question)

  if (question_text === 'Give details') { // HOLD YOUR NOSE, THIS IS STINKY
    dependencies.push({
      subject_question_uuid: question.question_schema_uuid,
      trigger_question_uuid: previousQuestion.question_schema_uuid,
      trigger_answer_value: 'Y',
      dependency_start: '2020-11-30 14:50:00'
    })
  }

  return question
}

function addGroupQuestion(content_uuid, content_type, group_uuid, validation) {

  const questionGroup = {
    question_group_uuid: questionGroupUuids(content_uuid),
    content_uuid: content_uuid,
    content_type: content_type,
    group_uuid: group_uuid,
    display_order: questionGroups.filter(qg => qg.group_uuid === group_uuid).length + 1,
    mandatory: true,
    validation: JSON.stringify(validation)
  }
  questionGroups.push(questionGroup)
}

function answerType(answerField) {
  const lines = answerField.split('\n')
  if (lines.length === 1 && lines[0] === 'Y/N')
    return yes_no

  if (lines.length !== 1) {
    const type = lines[0].toLowerCase()
    if (type.match(/drop-?down/))
      return ['dropdown', answerSchemaGroup(lines)]
    if (type.match(/radio/))
      return ['radio', answerSchemaGroup(lines)]
  }

  if (answerField.match(/date/i))
    return ['date', null]
  return ['freetext', null]
}

function answerSchemaGroup(lines) {
  lines = lines.slice(1).map(line => line.replace(/(\r|\n)/, '').split('|')).map(([a, v]) => v ? [a, v] : [a, a.toLowerCase()])

  const name = lines.map(([a, v]) => a).join('_').replace(/ /g, '-').toLowerCase()
  const existing = answerSchemaGroups.find(a => a.answer_schema_group_code === name)
  if (existing) return existing.answer_schema_group_uuid

  const answerGroup = {
    answer_schema_group_uuid: uuid(),
    answer_schema_group_code: name,
    group_start: '2020-11-30 14:50:00',
    group_end: null
  }

  answerSchemaGroups.push(answerGroup)

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
    answerSchemas.push(answerSchema)
  }
  return answerGroup.answer_schema_group_uuid
}

///////////////////////////
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

function answerSchemaGroupSql() {
  return tableSql(
    'answer_schema_group',
    ['answer_schema_group_uuid', 'answer_schema_group_code', 'group_start', 'group_end' ],
    answerSchemaGroups
  )
}

function answerSchemaSql() {
  return tableSql(
    'answer_schema',
    ['answer_schema_uuid', 'answer_schema_code', 'answer_schema_group_uuid', 'answer_start', 'answer_end', 'value', 'text'],
    answerSchemas
  )
}

function groupingSql() {
  const nonEmptyGroups = groups.filter(g =>
    (questionGroups.filter(qg => qg.group_uuid === g.group_uuid).length !== 0)
  )

  return tableSql(
    'grouping',
    ['group_uuid', 'group_code', 'heading', 'subheading', 'help_text', 'group_start', 'group_end'],
    nonEmptyGroups
  )
}

function questionsSql() {
  return tableSql(
    'question_schema',
    ['question_schema_uuid', 'question_code', 'oasys_question_code', 'question_start', 'question_end', 'answer_type', 'answer_schema_group_uuid', 'question_text', 'question_help_text', 'external_source'],
    questions
  )
}

function questionGroupSql() {
  const emptyGroups = groups
    .map(g => g.group_uuid)
    .filter(group_uuid => (questionGroups.filter(qg => qg.group_uuid === group_uuid).length === 0))

  const nonEmptyQuestionGroups = questionGroups.filter(qg => !emptyGroups.includes(qg.content_uuid))

  return tableSql(
    'question_group',
    ['question_group_uuid', 'content_uuid', 'content_type', 'group_uuid', 'display_order', 'mandatory', 'validation'],
    nonEmptyQuestionGroups
  )
}

function dependenciesSql() {
  return tableSql(
    'question_dependency',
    ['subject_question_uuid', 'trigger_question_uuid', 'trigger_answer_value', 'dependency_start'],
    dependencies
  )
}

///////////////////////////////////////////
function loadSpreadsheet(filename) {
  const input = fs.readFileSync(filename)
  const all_records = parse(input, {
    columns: false,
    relax_column_count: true,
    skip_empty_lines: true
  })
  .filter(record => record.join('')) // remove lines with no content

  const headers = findHeaders(all_records[1])
  const records = all_records.slice(2)

  patchInAddress(records, headers)
  return { headers, records }
}

function loadExternals(filename) {
  const input = fs.readFileSync(filename)
  const external_lines = parse(input, {
    columns: false
  }).map(fields => fields.slice(1)) // title is just for human information
  return Object.fromEntries(external_lines)
}

function findHeaders(headers) {
  const TITLE = headers.findIndex(field => field.match(/^question/i))
  const REF = headers.findIndex(field => field.match(/content.*ref/i))
  const QUESTION = headers.findIndex(field => field.match(/proposed.*wording/i))
  const ANSWER_TYPE = headers.findIndex(field => field.match(/input type/i))
  const OASYS_REF = headers.findIndex(field => field.match(/oasys ref/i))
  const ERROR_SUMMARY = headers.findIndex(field => field.match(/error summary/i))
  const ERROR_MESSAGE = headers.findIndex(field => field.match(/error message/i))

  if ([TITLE, REF, QUESTION, ANSWER_TYPE, OASYS_REF].includes(-1)) {
    console.error(`${csvfile} does not look like I expect!`)
    process.exit(-1)
  }
  return { TITLE, REF, QUESTION, ANSWER_TYPE, OASYS_REF, ERROR_MESSAGE, ERROR_SUMMARY }
}

function patchInAddress(records, headers) {
  const addressIndex = records.findIndex(r => r[headers.TITLE] === 'Address')
  if (addressIndex === -1)
    return

  // convert single line of address into five lines + postcode
  const addressLine = records[addressIndex]
  addressLine[headers.TITLE] = 'address_line_1'
  const addressLines = [addressLine]
  for (let i = 1; i !== 6; ++i) {
    const notlast = i !== 5;
    addressLines.push(addressLine.map(f => f))
    addressLines[i][headers.TITLE] = notlast ? `address_line_${i+1}` : 'post_code'
    addressLines[i][headers.REF] = `8.${i+1}`
    addressLines[i][headers.QUESTION] = notlast ? '' : 'Post Code'
    records.splice(addressIndex+i, 0, addressLines[i])
  }
}
