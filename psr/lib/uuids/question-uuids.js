const ElementUuids = require('./element-uuids')

const uuidCsv = 'question-uuids.csv'
const questionUuids = ElementUuids(uuidCsv)

const alreadySeen = new Set();

module.exports = (ref, answerType, title) => {
  ref = `${ref}-${answerType}`
  if (alreadySeen.has(ref)) {
    console.error(`Duplicate question reference ${ref}`)
    ref = `${ref}-${title}`
  }
  alreadySeen.add(ref)
  return questionUuids.lookup(ref, title)
}

