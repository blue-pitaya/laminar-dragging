# Laminar dragging

Simple dragging logic library for [Laminar](https://laminar.dev/). Library is based on handling and wrapping pointer events and **don't** depend on HTML draggable attribute.

See [demo]() and [examples source](https://github.com/blue-pitaya/laminar-dragging/tree/master/example/src/main/scala/xyz/bluepitaya/example).

## Instalation

Currently library isn't published anywhere, but it will changes soon.

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

## Extensions (mappings)

Basic `Dragging.Event` is not so useful and was created to be extended. There are some builtin mappings to enable more features.

### Delta position

```scala
draggingModule.componentEvents(id).map(DeltaDragging.getMapping())

case class Event(e: dom.PointerEvent, kind: DragEventKind, deltaPos: Vec2f)
```

This module extends drag events to add information about relative position to position where dragging started. This can be easily used for "classic" dragging. See example `DeltaExample2.scala`.

### Relative position

```scala
draggingModule.componentEvents(id).map(RelativeDragging.getMapping(container))
draggingModule.componentEvents(id).map(RelativeDragging.getMappingDynamic(getContainerFn))

case class Event(e: dom.PointerEvent, kind: DragEventKind, pos: Vec2f)
```

Add position relative to container element. 

First version takes size of container once, so if container is resized dragging will be acting strange.

Second version takes function `dom.Element => Boolean` as parameter and search for container element in event target hierarchy. That means size of container is always up to date, but dragging don't work outside container.

## Development

To run example page you need to:

1. Run `sbt` -> `project example` -> `~fastLinkJS`
2. Execute `yarn` (only once to install JS deps) -> `yarn dev` in `example/ui` dir.
