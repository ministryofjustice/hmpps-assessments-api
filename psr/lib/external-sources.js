const path = require('path')
const fs = require('fs')
const parse = require('csv-parse/lib/sync')

const externalsCsv = path.resolve(__dirname, '..', 'data', 'external-sources.csv')

function loadExternals() {
  const input = fs.readFileSync(externalsCsv)
  const external_lines = parse(input, {
    columns: false
  }).map(fields => fields.slice(1)) // title is just for human information
  return Object.fromEntries(external_lines)
}

const externals = loadExternals()

module.exports = ref => externals[ref]

