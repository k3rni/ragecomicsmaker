Rage Comics Maker
=======

Used to be a simple program to prepare ACV files from images (jpg/png). Now it's purpose is to create EPUB-format comic
books, from a set of images.


User guide
==========

For best usage, maximize the window (the program should start maximized).

Click "Open directory", and navigate to a directory of images. These must be named `screenXX.{jpg|png}`, with XX as
numbers increasing in page reading order. The numbers need not be contiguous.

The images will load into a list. Click any of them to load that screen into the frame editor.

Frame editor
------------

Drag an area with the left mouse button to create a frame, as many times as necessary. Frames must be larger than 5% of
width or height, otherwise they will be rejected as accidental clicks. Drag with right mouse button to pan the image
around. Mouse wheel scrolls the image vertically, or horizontally when <kbd>Shift</kbd> is held. Hold <kbd>Ctrl</kbd>
and use the wheel to zoom.

Frames are shown with their thumbnail in a list below the screens list. Delete a frame by clicking the X button on its
entry, reorder by drag-and-drop. Drop a frame on another one in the list to swap their places. Hover the cursor over a
frame to see it highlighted in the editor area.

Metadata editor
---------------

Edit metadata by switching to its tab. It shows a minimal subset of possible ePUB metadata. For the date field, use
either a 4 digit year, a year plus month, or a full year-month-day date. The *Full Pages* option is special, see the *
Saving* section below for an explanation.

HTML & CSS
-----------

If necessary, customize per-page HTML and the book CSS in their respective tabs. Each one contains a dropdown list with
useful snippets. The HTML page is actually a Mustache-style template, to be used as each page of the resulting comic.
Variables available are:

* `{{title}}` - book's title as set in metadata
* `{{image}}` - path to current screen image or frame image
* `{{width}}`, `{{height}}` - dimensions of current screen image or frame image

If left empty and unsaved, a simple default page is used, and no special CSS is added.

Saving
------

Clicking *Save* saves your changes, but does not generate the e-book yet. To do that, click "Generate Book".

This will produce a file named `TITLE - AUTHOR.epub` (as set in metadata) in the images' directory. It will also
generate all the subframes as individual images, before embedding them into the epub file. If `Full Pages` option was
set in the metadata tab, the book will have a single page with each full image, followed by all its frames on separate
pages. If it was unset, only the frames are inserted.

If a file named `cover.jpg` or `cover.png` exists in the images directory, it will be used as the book's cover.

Keyboard shortcuts
========

* <kbd>Ctrl+O</kbd>: show open-directory dialog
* <kbd>Ctrl+S</kbd>: save changes. Doesn't build the epub file.
* <kbd>F1</kbd>, <kbd>Ctrl+1</kbd>: switch to frame editor tab
* <kbd>F2</kbd>, <kbd>Ctrl+2</kbd>, <kbd>Ctrl+M</kbd>: switch to metadata tab
* <kbd>Ctrl+F</kbd>: fit image to editor frame, resetting zoom. Resizing the window will also fit and reset.
* <kbd>PageUp</kbd>, <kbd>PageDown</kbd> (with focus in editor tab): jump to previous or next screen. When the screens
  list is focused, these behave differently, and jump around the list.
* <kbd>Ctrl+0</kbd> (the zero digit): toggle dark mode

On Apple computers, use the <kbd>Command</kbd> key instead of <kbd>Ctrl</kbd>.

## EPUB files generated

The `epub-creator` library used produces EPUB3 files. They have
a [mandatory navigation section](http://idpf.org/epub/301/spec/epub-contentdocs.html#sec-xhtml-nav), which is
automatically inserted before all content. While it is set to hidden, it may still be visible as a blank page in your
reader. The `Illustrator` metadata property is generated as
a [`dc:creator`](https://www.w3.org/publishing/epub3/epub-packages.html#sec-opf-dccreator) element, annotated with
an `ill` [role](https://www.w3.org/publishing/epub3/epub-packages.html#sec-role). Similarly, ISBN is a `dc:identifier`
with `scheme` set to `isbn`.