import { Fragment, type ReactNode } from 'react'

interface JsonViewerProps {
  data: unknown
  maxHeight?: string
}

function renderValue(value: unknown, indent: number): ReactNode {
  const pad = '  '.repeat(indent)
  const padInner = '  '.repeat(indent + 1)

  if (value === null) {
    return <span className="json-null">null</span>
  }
  if (typeof value === 'boolean') {
    return <span className="json-boolean">{String(value)}</span>
  }
  if (typeof value === 'number') {
    return <span className="json-number">{String(value)}</span>
  }
  if (typeof value === 'string') {
    return <span className="json-string">{JSON.stringify(value)}</span>
  }
  if (Array.isArray(value)) {
    if (value.length === 0) return '[]'
    return (
      <Fragment>
        {'[\n'}
        {value.map((item, i) => (
          <Fragment key={i}>
            {padInner}
            {renderValue(item, indent + 1)}
            {i < value.length - 1 ? ',' : ''}
            {'\n'}
          </Fragment>
        ))}
        {pad}{']'}
      </Fragment>
    )
  }
  if (typeof value === 'object') {
    const entries = Object.entries(value as Record<string, unknown>)
    if (entries.length === 0) return '{}'
    return (
      <Fragment>
        {'{\n'}
        {entries.map(([k, v], i) => (
          <Fragment key={k}>
            {padInner}
            <span className="json-key">{JSON.stringify(k)}</span>
            {': '}
            {renderValue(v, indent + 1)}
            {i < entries.length - 1 ? ',' : ''}
            {'\n'}
          </Fragment>
        ))}
        {pad}{'}'}
      </Fragment>
    )
  }
  return String(value)
}

export default function JsonViewer({ data, maxHeight }: JsonViewerProps) {
  return (
    <pre
      className="overflow-auto text-sm leading-relaxed text-slate-300"
      style={maxHeight ? { maxHeight } : undefined}
    >
      <code>{renderValue(data, 0)}</code>
    </pre>
  )
}
