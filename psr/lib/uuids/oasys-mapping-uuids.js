const ElementUuids = require('./element-uuids')

const uuidCsv = 'oasys-mapping-uuids.csv'
const oasysMapping = ElementUuids(uuidCsv)

module.exports = (questionUuid, oasysQuestion) =>
  oasysMapping.lookup(questionUuid, `${oasysQuestion.ref_section_code}-${oasysQuestion.logical_page}-${oasysQuestion.ref_question_code}`)

