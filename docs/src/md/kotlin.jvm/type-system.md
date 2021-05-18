## Type system

### Type kinds

#### Type parameters

On the JVM platform, bounded type parameter with regular bounds should satisfy the following set of conditions:

* $F$ is a type parameter of type constructor $T$
* $\forall i \in [1,n]: B_i$ must be concrete, non-type-parameter, well-formed type
* No more than one of $B_i$ may be a class type
* Additionally, $\forall i \in [1,n]: B_i$ is not a parameterized or specialized [array type][Array types]

> Note: the last condition mirrors the JVM platform restriction on array types not allowed in upper bound wildcards.
