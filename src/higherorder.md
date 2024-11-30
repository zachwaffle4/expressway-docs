# Functions as Objects

In a lot of Expressway's code, you might notice that a lot of classes and functions take parameters of type `Supplier`. 
`Supplier<T>` is a functional interface that has a single method, 
`get()`, which returns a value of type `T`. 
`T` can be any type, allowing us to specify the return type of the `get` method when we create a `Supplier` object,
instead of when we define the `Supplier` interface.

This is useful because it allows us to pass functions as parameters to other functions,
which can be very powerful when you want to create more flexible and reusable code.
For example, to create an `InitLoopCondAction`, 
you need to pass a `Supplier<Boolean>` that returns `true` when the action needs to be repeated.

You might notice that in the examples in the [Base Actions](creating-actions/base-actions.md) section,
we don't actually define a `Supplier` object when we create an `InitLoopCondAction`,
we instead create a function that returns a `Supplier` object.

