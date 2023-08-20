# Laminar dragging

Simple dragging logic library for [Laminar](https://laminar.dev/). Library is based on handling and wrapping pointer events fired while dragging. It **doesn't** depend on HTML draggable attribute.

See [demo](https://blue-pitaya.github.io/laminar-dragging/) and [examples source](https://github.com/blue-pitaya/laminar-dragging/tree/master/example/src/main/scala/dev/bluepitaya/example).

## Instalation

For Laminar v16.0.0:

```scala
libraryDependencies += "dev.bluepitaya" %%% "laminar-dragging" % "1.1"
```

## Basic example

```scala
def component() = {
  val draggingModule: Dragging.DraggingModule[String] = Dragging
    .createModule[String]()
  val eventKindLabel = Var[String]("Drag the square!")

  div(
    // bindings for document events, put them in "parent" of dragging elements
    draggingModule.documentBindings,
    div(
      // binding function for dragging elements
      draggingModule.componentBindings("someObj"),
      // event stream of dragging events
      draggingModule.componentEvents("someObj") -->
        Observer[Dragging.Event] { event =>
          event match {
            // e is dom.PointerEvent
            case Dragging.Event(e, DragEventKind.Start) => eventKindLabel
                .set("START")
            case Dragging.Event(e, DragEventKind.Move) => eventKindLabel
                .set("MOVE")
            case Dragging.Event(e, DragEventKind.End) => eventKindLabel
                .set("END")
          }
        },
      // just some styles
      Seq(width("100px"), height("100px"), backgroundColor("green"))
    ),
    p(child.text <-- eventKindLabel),
    // current dragging id
    p(child.text <-- draggingModule.currentDraggingIdSignal.map(_.toString()))
  )
}
```

Function `Dragging.createModule[A]()` creates Laminar event handlers under the hood and return module defined below. Type parameter A is type of unique id for each dragging component.

```scala
/** Dragging toolbox.
  *
  * @param documentBindings
  *   Pointer event bindings for document.
  * @param componentBindings
  *   Function for creating pointer event bindings in dragging element.
  * @param componentEvents
  *   Function for creating event stream of draggin events.
  * @param currentDraggingIdSignal
  *   Signal of current dragging id.
  */
case class DraggingModule[A](
    documentBindings: Seq[Binder.Base],
    componentBindings: A => Seq[Binder.Base],
    componentEvents: A => EventStream[Event],
    currentDraggingIdSignal: Signal[Option[A]]
)
```

## Utilities

`def getRelativePosition(e: Event, container: dom.Element): Vec2f` - Get dragging position relative to other element. See `RelativeExample.scala` for use case.

`def withDeltaPosition(componentEvents: EventStream[Event]): EventStream[(Event, Vec2f)]` - Append dragging position relative to start dragging position. See `DeltaExample.scala` and `DeltaExample2.scala` for use cases.

## Development

To run example page you need to:

1. Run `sbt` -> `project example` -> `~fastLinkJS`
2. Execute `yarn` (only once to install JS deps) -> `yarn dev` in `example/ui` dir.
