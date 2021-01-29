const loadAssessmentCsv = require('./lib/load-assessment-csv')
const AssessmentSql = require('./lib/assessment-sql')

if (process.argv.length !== 3) {
  return console.error("Usage: node assessment-generator <csv-file>")
}

const { headers, records } = loadAssessmentCsv(process.argv[2])

const assessment = AssessmentSql('Pre-Sentence Assessment', headers)
let currentGroup = null

for (const record of records) {
  if (assessment.isGroup(record)) {
    currentGroup = assessment.addGrouping(record)
    continue
  }

  if (!currentGroup)
    continue

  assessment.addQuestion(record)
}

assessment.addDependencyUuids()
assessment.toSql()

