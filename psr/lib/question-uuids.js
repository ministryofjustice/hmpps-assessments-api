const path = require('path')
const fs = require('fs')
const parse = require('csv-parse/lib/sync')
const { v4: uuid } = require('uuid')

const uuidCsv = path.resolve(__dirname, '..', 'data', 'question-uuids.csv')

function loadUuids() {
  const input = fs.readFileSync(uuidCsv)
  const external_lines = parse(input, {
    columns: false
  }).map(fields => fields.slice(1)) // title is just for human information
  return Object.fromEntries(external_lines)
}

function appendUuid(title, ref, uuid) {
  fs.appendFileSync(uuidCsv, `${title},${ref},${uuid}\n`)
}

const externals = loadUuids()

module.exports = (ref, title) => {
  if (!externals[ref]) {
    externals[ref] = uuid()
    appendUuid(title, ref, externals[ref])
  }
  return externals[ref]
}

