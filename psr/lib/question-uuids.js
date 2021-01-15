const { v4: uuid } = require('uuid')
const { DataFile } = require('./data-files')

const uuidCsv = 'question-uuids.csv'

function loadUuids() {
  return DataFile.load(uuidCsv)
}

function appendUuid(title, ref, uuid) {
  DataFile.append(uuidCsv, title, ref, uuid)
}

const externals = loadUuids()

module.exports = (ref, title) => {
  if (!externals[ref]) {
    externals[ref] = uuid()
    appendUuid(title, ref, externals[ref])
  }
  return externals[ref]
}

