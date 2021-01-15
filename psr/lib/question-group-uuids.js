const ElementUuids = require('./element-uuids')

const uuidCsv = 'question-group-uuids.csv'
const questionGroupUuids = ElementUuids(uuidCsv)

module.exports = (content_uuid) =>
  questionGroupUuids.lookup(content_uuid, '-')

