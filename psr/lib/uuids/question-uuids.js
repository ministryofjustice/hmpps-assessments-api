const ElementUuids = require('./element-uuids')

const uuidCsv = 'question-uuids.csv'
const questionUuids = ElementUuids(uuidCsv)

module.exports = (ref, title) =>
  questionUuids.lookup(ref, title)

