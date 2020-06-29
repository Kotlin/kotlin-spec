fun main() {
    x<y>(false)
    1<2>(false)
    1<x>(false)
    y<1>(false)
    y<null>(false)
    null<y>(false)
    null<1>(false)
    1<null>(false)
    x<true>(false)
    true<x>(false)
    true<null>(false)
    null<null>(null)

    ""<"">("")
    ""<true>("")
    true<"">("")
    true<'a'>('b')
    'a'<'b'>('c')
    'a'<"">('b')
    'a'<null>('b')

    1L<5f>(1L)
    0<0>(0)

    1uL<5L>(1Ul)
    1uL<-5L>(1Ul)
    -1uL<-5L>(-1Ul)
    -1uL<5L>(-1Ul)

    x<y>1
    x<y>x
    1<2>2
    1<x>x
    y<1>null
    y<null>false
    null<y>y
    null<1>0
    1<null>null
    x<true>true
    true<x>-19
    true<null>10L
    null<null>null

    ""<"">""
    ""<true>'a'
    true<"">false
    true<'a'>'b'
    'a'<'b'>0
    'a'<"">false
    'a'<null>'b'

    1L<5f>null
    0<0>false

    1uL<5L>0L
    -1uL<-5L>-1Ul
    -1uL<5L>-1Ul
    -1uL<5L>1Ul
    1uL<-5L>1Ul
    1uL<-5L>-1Ul
    1uL<5L>-1Ul
}