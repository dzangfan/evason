# Evason -- Evaluative JSON

Evason is a language enables JSON to be computable by adding a
`lambda` data type. Introducing minimal new features, we are seeking
to express the maximum by Evason.

```javascript
{ concat: # ([]           rhs) => rhs
          | ([fst ...rst] rhs) => [fst ...(concat rst rhs)]
          #
, reverse: # [] => []
           | [fst ...rst] => (concat (reverse rst) [fst])
           #
}
```

## Features

### λ + JSON

The (basically) only data type added in JSON to enable the computability is lambda, accompanyed by two syntax extensions: *application* and *variable*.

```javascript
{ constantlyNull: # x => null #
, result: (constantlyNull 10)
}
```

In this example, `# x => null #` is a constant function that always returns `null`; `constantlyNull` is a variable, and in the second line, `result` uses this variable and applies its value(the lambda) to `10`.

### Pattern Matching

Pattern matching is a frequently used method to identify and extract data in functional programming. See our first example in this page for its usage.

Pattern matching in Evason is basically "work as you are thinking". It appears and can only appear in parameter part of a lambda expression, namely the left of `=>`. Here is some basic rules of matching:

1. Primitive values, like `1`, `"string"`, and `true`, match exactly with themselves.
2. Compound values, like `{ x: 10 }`, `[1, 2]`, match with another when they have the same type and each field is matching. Specially, a pattern of object can match an object whose fields is a "superset" of the pattern.
3. A variable in pattern can match everything, and the matched value can be refered in the body of lambda expression.
4. lambda and application cannot appear in pattern, and therefore they cannot match anything.

Borrowed from JavaScript, we use `...` + `variable` to match "rest" components of a object or an array. They should only appear in the end. Furthermore, since code like `{ x: x }` is so common in Evason, it can be simplified to `{ x }`. Here is a example:

```javascript
{ map: # { fun, list: [] } => []
       | { fun, list: [fst, ...rst]} =>
         [(fun fst), ...(map { fun, list: rst })]
       #
, result: (map { fun: # x => [ x ] #, list: [1, 2, 3]})
}
```

### Currying

A function can only be applied by exactly one argument. But you can "chain" lambda-s to make your function virtually able to take many arguments:

```javascript
{ take3: # x => # y => # z => [x, y, z] # # #
, result: (((take3 "Where") "is") "Alice") }
```

This feature, called [currying](https://en.wikipedia.org/wiki/Currying), is common in functional programming partially because of its flexibility of cooperate with other functions. For example, we can easily define a new function by provide some but not all arguments to `take3`:

```javascript
{ whereIs: ((take3 "Where") "is")
, result: (whereIs "Alice") }
```

or pass it to `map` directly, which we have defined in previous section:

```javascript
(map { fun: ((take3 "Where") "is")
     , list: [ "Alice", "Bob", "Carol" ]})
```

However, notations like `# a => b => c => ... ###` and `(((f a) b) c)` is very annoying. Evason supports a syntax sugar for the currying which looks like `# (a b c) => ... #` and `(f a b c)`. So examples above can be written in the following way, which has exactly equal effect:

```javascript
{ take3: # (x y z) => [x, y, z] #
, result: (map { fun: (take3 "Where" "is")
               , list: [ "Alice", "Bob", "Carol" ]})}
```

### World of Function

Everything in Evason is a function, even including primitive data type like string and number. The specific meaning depends on specfic data type. You have been familiar with data type `lambda`, but before we introduce functional meanings of other types, we would like to show you another (and the last) data type that Evason added to JSON -- Symbol. A symbol is basically a string, denoted by `'name`. Like many languages supporting meta-programming, which use symbols as meta data, Evason always uses symbols to denote field of object or concept of `enum` in languages like C or Java. Here is respective meanings of data types when they are used as a function:

- Object: Take a symbol as a field name, return the corresponding value in the object.
  + e.g. `({ x: 10, y: 20 } 'x)` = `10`
- Array: Take a number as a index, return the value in that position. Start at `0`.
  + e.g. `([0 1 2 3] 2)` = `2`
- String: Take another string, return a string concatenating the current and the other
  + e.g. `("Hello " "world!")` = `"Hello world!"`
- Number: Take a lambda, repeat calling it n times, and return a array composed by what the lambda returned, where n is its value. The lambda will receive a integer indicating how many times have been called. 
  + e.g. `(3 # x => x#)` = `[0, 1, 2]`
- Boolean: Take two currified value of any type, return the first if its value is true, or the second otherwise.
  + e.g. `(true 1 0)` = `1`, `(false 1 0)` = `0`
- Null: Take a string, report a unrecoverable error with the string as a message.
  + e.g. `(null "Bang!")` causes a error.
- Symbol: Take a object, determine whether a field named its value is present in the object.
  + e.g. `('name { age: 30 })` = `false`, `('age { age: 30 })` = `true`

Note that because of the currying, when used as a function, some data types can be combined. For example:

- `("Where " "are " "you?")` = `"Where are you?"`
- `([{ name: "Alice" }, { name: "Bob" }, { name: "Carol" }] 1 'name)` = `"Bob"`

### Other Sugar

BIIIIG NEWS!! Comma(,) in JSON is syntactic OPTIONAL! Well, I know it is not that amazing actually, but this fact can simplify your typing somewhat. In one word, in Evason, all comma is optional. So `[a, b, c]` is exactly equal to `[a b c]`.

## Installation

`git clone` this repo, `mvn package` it, and `java -jar` the `target/evason-0.0.1-SNAPSHOT.jar`. Java 16 or higher is required and Maven 3.8.1 or higher is prefered.

## PAQ

Here are some answers for possibly asked questions, and some technologies may be useful if you (really) want to use Evason to program. Note that since Evason borrowed Scheme's syntactic highlight (currently), we will use `;;` as the comment notation in following sections.

### Q: How to declare local variables?

There is two styles of local variables, I call them `let`-style and `where`-style, you can call them poor man's local variables:

```scheme
;; let x = 10
;;     y = 20
;; in [x, y]
({ x: 10
 , y: 20
 , result: [x, y]
 } 'result)
```

```scheme
;; [x, y]
;; where
;;   x = 10
;;   y = 20
(# {x y} => [x y] #
 { x: 10
 , y: 20})
```

### T: A fancy step-by-step evaluation

By defining following function, you can get a exclusive expreience of step-by-step computation. Subscribe now!

```scheme
{
  begin: # init => { then: # fun => (begin (fun done)) #
                   , done: init }
         #
}
```

Usage:

```scheme
(begin "man" 'then # s => ("a young " s) #
             'then # s => (s " came out of") #
             'then # s => (s " a garret") #
             'then # s => ("On an exceptionally hot evening " s) #
             'done)
```

equal to

```scheme
"On an exceptionally hot evening a young man came out of a garret"
```

### T: `if`, `else if` and `else`

To express mutiple conditions, you can just pass a condition selection as the second argument of a condition selection. Recall that boolean value itself can be used as a operator:

```scheme
{ Joe: { name: "Joe", age: 1000 }
, ageRange: # { age } => ((lessThan age 18)  'child
                          ((lessThan age 60) 'adult
                                             'old))
            #
, result: (ageRange Joe)
}
```

If you think this code is obscure, ambiguous, and unacceptable, here is another version:

```javascript
{ compose: # (f g x) => (f (g x)) #
, try: # first => { or: # next => (try (compose first next)) #
                  , else: first
                  }
       #
}
```

`compose` is a auxiliary operation which combines two function together, `try` takes a chain of partial condition application and choose the right one:

```scheme
{ Joe: { name: "Joe", age: 22 }
, ageRange: # { age } => (try ((lessThan age 18) 'child)
                          'or ((lessThan age 60) 'adult)
                          'else                  'old)
            #
, result: (ageRange Joe)
}
```
