const ElementUuids = require('./element-uuids')

const uuidCsv = 'answer-schema-group-uuids.csv'
const answerGroupUuids = ElementUuids(uuidCsv)

module.exports = (schemaGroupCode) =>
  answerGroupUuids.lookup(schemaGroupCode, '-')

