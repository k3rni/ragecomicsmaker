Rage Comics Maker
=======

Simple program to prepare ACV files from images (jpg/png)


Usage
=====

For best usage, maximize the window.

Click "Select directory", and navigate to a directory of images. 
These should be named `screenXX.{jpg|png}`, with XX as numbers increasing in
page reading order. The numbers need not be contiguous.

The images will load into a list. Click any of them to open the frame drawing interface.
By drag-and-drop, mark an area to be used as frame, as many times as necessary.
Use the list next to the image to manage and reorder frames.

Edit metadata by clicking its button, the form displayed is a minimal subset of
possible ePUB metadata. For the date field, use either a 4 digit year, a year plus month,
or a full year-month-day date.

Clicking "Build" will do the following:

1. Generate frames as separate images in a `clips/` subdirectory. These are always PNG files for now.
2. Try to find a `page.xhtml` file in the images directory. This is a mustache-style template of a HTML file
to be used as each page of the resulting comic. Currently, the variables available to that template
are only `{{title}}` (as set in metadata) and `{{image}}`, containing the current page, or subframe's image path.
If not found in the images directory, a simple default will be used.
3. Produce `book.epub`  in the selected directory. Pages are generated from the source images,
each followed by its subframes. There is one page for each image, containing the full image,
followed by pages containing only the frame images.

 
