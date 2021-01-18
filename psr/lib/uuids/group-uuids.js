const ElementUuids = require('./element-uuids')

const uuidCsv = 'group-uuids.csv'
const groupUuids = ElementUuids(uuidCsv)

module.exports = ref =>
  groupUuids.lookup(ref, ref)

