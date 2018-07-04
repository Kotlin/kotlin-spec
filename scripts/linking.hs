#!/usr/bin/env runhaskell
module Linking where

import Text.Pandoc.JSON

-- This will be done for all the blocks in the document (including nested blocks)
insertLinks :: Block -> Block
-- Paragraph, put it in a <div>, process its contents
insertLinks (Para contents) = Div ("", ["paragraph"], []) [Para $ processSentences contents]
-- Block of text (not a paragraph). An example would be the contents of a list item.
insertLinks (Plain contents) = Plain $ processSentences contents
-- Do not change anything else
insertLinks somethingElse = somethingElse

-- Take a list of inlines, break them into sentences, put each sentence in a <span>
processSentences :: [Inline] -> [Inline]
processSentences ss = Span ("", ["sentence"], []) <$> splitSentences ss

-- Gracefully stolen from Pandoc Man Writer module
-- Modified to not throw out spaces after sentenses
-- Take a list of inlines, return the sentence and the rest of the list
breakSentence :: [Inline] -> ([Inline], [Inline])
breakSentence [] = ([],[])
breakSentence xs =
  let isSentenceEndInline (Str ys@(_:_)) | last ys == '.' = True
      isSentenceEndInline (Str ys@(_:_)) | last ys == '?' = True
      isSentenceEndInline (Str ys@(_:_)) | last ys == '!' = True
      isSentenceEndInline LineBreak      = True
      isSentenceEndInline _              = False
      (as, bs) = break isSentenceEndInline xs
  in  case bs of
           []             -> (as, [])
           [c]            -> (as ++ [c], [])
           (c:Space:cs)   -> (as ++ [c, Space], cs)
           (c:SoftBreak:cs) -> (as ++ [c, SoftBreak], cs)
           (Str ".":Str (')':ys):cs) -> (as ++ [Str ".", Str (')':ys)], cs)
           (x@(Str ('.':')':_)):cs) -> (as ++ [x], cs)
           (LineBreak:x@(Str ('.':_)):cs) -> (as ++ [LineBreak], x:cs)
           (c:cs)         -> (as ++ [c] ++ ds, es)
              where (ds, es) = breakSentence cs

-- Take a list of inlines, return a list of sentences
splitSentences :: [Inline] -> [[Inline]]
splitSentences [] = []
splitSentences xs =
  let (sent, rest) = breakSentence xs
  in sent : splitSentences rest

-- toJSONFilter magically returns a `main`-compatible filter function that 
-- applies a `insertLinks` to everything in the document
main = toJSONFilter insertLinks
