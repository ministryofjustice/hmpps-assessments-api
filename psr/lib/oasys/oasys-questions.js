const { DataFile } = require('../data-files')

function loadOasysCsv() {
  const all_records = DataFile.csv('oasys-layer1-export.csv')

  // find line that contains headers
  const columns = findColumns(all_records[0])
  const records = all_records.slice(1, all_records.length)

  const questions = records.map(record => {
    const question = { }
    for (const [field, column] of Object.entries(columns))
      question[field] = record[column]
    return question
  })

  return new OasysQuestions(questions)
}

function findColumns(headers) {
  const REF_SECTION_CODE = headers.findIndex(field => field.match(/REF_SECTION_CODE/))
  const REF_QUESTION_CODE = headers.findIndex(field => field.match(/REF_QUESTION_CODE/))
  const MANDATORY_IND = headers.findIndex(field => field.match(/MANDATORY_IND/))
  const ANSWER = headers.findIndex(field => field.match(/ANSWER/))
  const INPUT_TYPE = headers.findIndex(field => field.match(/INPUT_TYPE/))
  const LOGICAL_PAGE = headers.findIndex(field => field.match(/LOGICALPAGE/))
  const REF_SECTION_QUESTION = headers.findIndex(field => field.match(/REF_SECTION_QUESTION/))

  if ([REF_SECTION_CODE, REF_QUESTION_CODE, MANDATORY_IND, ANSWER, LOGICAL_PAGE].includes(-1)) {
    console.error(`${csvfile} does not look like I expect!`)
    process.exit(-1)
  }
  return {
    REF_SECTION_CODE,
    LOGICAL_PAGE,
    REF_QUESTION_CODE,
    MANDATORY_IND,
    ANSWER,
    INPUT_TYPE,
    REF_SECTION_QUESTION
  }
}

class OasysQuestions {
  constructor(questions) {
    this.questions = questions
  }
}

module.exports = loadOasysCsv
