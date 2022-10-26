# Evason -- Evaluative JSON

Evason is a language enables JSON to be computable by adding a
`lambda` data type. Introducing minimal new features, we are seeking
to express the maximum by Evason.

```javascript
{ concat: # ([]           rhs) => rhs
          | ([fst ...rst] rhs) =>
              (# { concated } => [fst ...concated] #
               { concated: (concat rst rhs) })
          #
, reverse: # [] => []
           | [fst ...rst] => (concat (reverse rst) [fst])
           #
}
```