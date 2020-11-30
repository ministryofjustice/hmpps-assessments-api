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
const psr = addGrouping(['Pre-Sentence Report'])
console.log(psr)

for (let index = 1; index !== records.length; ++index) {
  const record = records[index];
  if (isGroup(record))
    addGrouping(record)
}

console.log(groupingSql())

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

function insertSql(table, fields) {
  return `INSERT INTO ${table} (${fields.join(', ')})\nVALUES\n`
}

function valueSql(fields, obj) {
  const values = fields
	.map(field => obj[field] ? obj[field] : null)
        .map(value => (typeof value === 'string') ? `'${value}'` : value)
        .map(value => (value !== null) ? value : 'null')
        .join(', ')
  return `(${values})`
}

function groupingSql() {
  const fields = ['group_id', 'group_uuid', 'group_code', 'heading', 'subheading', 'help_text', 'group_start', 'group_end']
  const insert = insertSql('grouping', fields)
  const values = groups.map(group => valueSql(fields, group)).join('\n  ')
  return insert + values
}
