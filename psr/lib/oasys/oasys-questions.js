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
  const ref_section_code = headers.findIndex(field => field.match(/REF_SECTION_CODE/))
  const ref_question_code = headers.findIndex(field => field.match(/REF_QUESTION_CODE/))
  const mandatory_ind = headers.findIndex(field => field.match(/MANDATORY_IND/))
  const answer = headers.findIndex(field => field.match(/ANSWER/))
  const input_type = headers.findIndex(field => field.match(/INPUT_TYPE/))
  const logical_page = headers.findIndex(field => field.match(/LOGICALPAGE/))
  const ref_section_question = headers.findIndex(field => field.match(/REF_SECTION_QUESTION/))

  if ([ref_section_code, ref_question_code, mandatory_ind, answer, logical_page].includes(-1)) {
    console.error(`${csvfile} does not look like I expect!`)
    process.exit(-1)
  }
  return {
    ref_section_code,
    logical_page,
    ref_question_code,
    mandatory_ind,
    answer,
    input_type,
    ref_section_question
  }
}

class OasysQuestions {
  constructor(questions) {
    this.questions = questions
  }

  lookup(questionCode) {
    const candidates = this.questions.filter(question => question.ref_question_code === questionCode)
    if (candidates.length === 0)
      return console.warn(`Could not find OASys question ${questionCode}`)
    if (candidates.length > 1)
      return console.warn(`Multiple OASys questions match ${questionCode} - ${candidates.map(q => `${q.ref_section_code} ${q.ref_question_code}`)}`)
    return candidates[0]
  }
}

module.exports = loadOasysCsv
