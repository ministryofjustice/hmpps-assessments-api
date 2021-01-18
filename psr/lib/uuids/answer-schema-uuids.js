const ElementUuids = require('./element-uuids')

const uuidCsv = 'answer-schema-uuids.csv'
const answerUuids = ElementUuids(uuidCsv)

module.exports = (schemaCode, answerGroupCode) =>
  answerUuids.lookup(`${schemaCode}-${answerGroupCode}`, '-')

