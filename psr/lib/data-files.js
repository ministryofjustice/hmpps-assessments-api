const path = require('path')
const fs = require('fs')
const parse = require('csv-parse/lib/sync')

function loadFile(
  filePath,
  options = { columns: false }
) {
  const input = fs.readFileSync(filePath)
  return parse(input, options)
}

function dataPath(filename) {
  return path.resolve(__dirname, '..', 'data', filename)
}

function loadDataFile(filename) {
  const contents = loadFile(dataPath(filename))
    .map(fields => fields.slice(1)) // title is just for information
  return Object.fromEntries(contents)
}

function appendDataFile(filename, title, ref, value) {
  fs.appendFileSync(
    dataPath(filename),
    `${title},${ref},${value}\n`
  )
}

module.exports = {
  loadFile: loadFile,
  DataFile: {
    load: loadDataFile,
    append: appendDataFile
  }
}
