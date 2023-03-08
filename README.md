# Laminar drag logic

This repo contains simple logic for dragging HTML element inside Laminar enviroment. Main module creates handlers for pointer events of dragging element and document, it's not using draggable HTML prop. Pointer events are wrapped into special drag events that can be passed to any Laminar Observable (e.g. EventBus or Observer).

Drag events are simply wrappers for pointer events, but they also contains info about drag state (start dragging, moving, dropping).

## Extensions

There are some predefined extensions to handle drag events.

### Delta position

This module extends drag events to add information about relative position to position where dragging started.
