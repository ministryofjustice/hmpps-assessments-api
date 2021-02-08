const loadAssessmentCsv = require('./lib/load-assessment-csv')
const AssessmentSql = require('./lib/assessment-sql')
const program = require('commander')

program
  .option('-i, --incremental', 'Generate SQL only for new groups, questions, and answers')
  .option('-t, --title <name>', 'The title of the top-level group')
  .usage('[options] <csv-file>')

const command = program.parse(process.argv)
const fileArgs = command.args
const options = command.opts()

if (fileArgs.length != 1) {
  return console.error(program.help())
}

const title = program.opts().title ?? 'Pre-Sentence Assessment'
const incremental = options.incremental
const { headers, records } = loadAssessmentCsv(fileArgs[0])

const assessment = AssessmentSql(title, headers, incremental)
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
assessment.addFilteredReferenceDataTargets()
assessment.toSql()
