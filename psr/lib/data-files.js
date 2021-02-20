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
function uuidPath(filename) {
  return path.resolve(__dirname, '..', 'data', 'uuids', filename)
}

function load(filepath) {
  const contents = loadFile(filepath)
    .map(fields => fields.slice(1)) // title is just for information
  return Object.fromEntries(contents)
}

function loadDataFile(filename) {
  return load(dataPath(filename))
}

function loadUuidFile(filename) {
  return load(uuidPath(filename))
}

function appendUuidFile(filename, title, ref, value) {
  fs.appendFileSync(
    uuidPath(filename),
    `${title.replace(/\r?\n|\r/g, '')},${ref},${value}\n`
  )
}

module.exports = {
  loadFile: loadFile,
  UuidFile: {
    load: loadUuidFile,
    append: appendUuidFile
  },
  DataFile: {
    load: loadDataFile
  }
}
