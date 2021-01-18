const ElementUuids = require('./element-uuids')

const uuidCsv = 'question-group-uuids.csv'
const questionGroupUuids = ElementUuids(uuidCsv)

module.exports = (contentUuid) =>
  questionGroupUuids.lookup(contentUuid, '-')

