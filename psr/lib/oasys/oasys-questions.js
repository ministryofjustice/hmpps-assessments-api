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

    question.answers = unfutzAnswer(question.answers, question.input_type)
    return question
  })

  return new OasysQuestions(questions)
}

function unfutzAnswer(answerString, inputType) {
  if (!answerString) return
  // unpack from string, into the kind of format the rest of the system wants
  const answers = answerString.split('|').map(answer => answer.split('/').join('|'))

  const arnInputType = mapInputType(answers, inputType)
  answers.unshift(arnInputType)

  return answers
}

function mapInputType(answers, inputType) {
  if (inputType === 'SELECT')
    return answers.length > 4 ? 'drop-down' : 'radio'
  return inputType.toLowerCase()
}

function findColumns(headers) {
  const ref_section_code = headers.findIndex(field => field.match(/REF_SECTION_CODE/))
  const ref_question_code = headers.findIndex(field => field.match(/REF_QUESTION_CODE/))
  const mandatory_ind = headers.findIndex(field => field.match(/MANDATORY_IND/))
  const answers = headers.findIndex(field => field.match(/ANSWERS/))
  const input_type = headers.findIndex(field => field.match(/INPUT_TYPE/))
  const logical_page = headers.findIndex(field => field.match(/LOGICALPAGE/))
  const ref_section_question = headers.findIndex(field => field.match(/REF_SECTION_QUESTION/))

  if ([ref_section_code, ref_question_code, mandatory_ind, answers, logical_page].includes(-1)) {
    console.error(`${csvfile} does not look like I expect!`)
    process.exit(-1)
  }
  return {
    ref_section_code,
    logical_page,
    ref_question_code,
    mandatory_ind,
    answers,
    input_type,
    ref_section_question
  }
}

class OasysQuestions {
  constructor(questions) {
    this.questions = questions
  }

  lookup(questionCode, fixedCode) {
    if (fixedCode)
      return fixedRef(fixedCode)

    if (!questionCode) return

    const codes = questionCode.split('/')
    const questionFilter = (codes.length === 1)
      ? question => question.ref_question_code === codes[0]
      : question => question.ref_section_code === codes[0] && question.ref_question_code === codes[1]

    const candidates = this.questions.filter(questionFilter)
    if (candidates.length === 0) {
      console.warn(`Could not find OASys question ${questionCode}`)
      return {
        ref_section_code: 'arn',
        ref_question_code: questionCode.replace(/[ ,\\'\\"]/g, '_')
      }
    }
    if (candidates.length > 1)
      return console.warn(`Multiple OASys questions match ${questionCode} - ${candidates.map(q => `${q.ref_section_code} ${q.ref_question_code}`)}`)
    return candidates[0]
  }
}

function fixedRef(fixedCode) {
  const [sectionCode, questionCode] = fixedCode.split('/')
  return {
    ref_section_code: sectionCode,
    ref_question_code: questionCode,
    fixed_field: true
  }
}

module.exports = loadOasysCsv
