try:
    libname = libraryOrResourceFile
    library = LibraryDoc(libname, argument, name)
    output_selected = output or '.'
    if _uploading(output_selected):
        file_path = os.path.join(tempfile.gettempdir(), 'libdoc_upload.xml')
        create_xml_doc(library, file_path)
        upload_xml_doc(file_path, output_selected)
        os.remove(file_path)
    else:
        format_selected = get_format(format, output_selected)
        if os.path.isdir(output_selected):
            output_selected = get_unique_path(os.path.join(output_selected, library.name), format_selected.lower())
        output_selected = os.path.abspath(output_selected)
        if format_selected == 'HTML':
            create_html_doc(library, output_selected, title, styles)
        else:
            create_xml_doc(library, output_selected)
except Information, msg:
    print msg
except DataError, err:
    print err, '\n\nTry --help for usage information.'
except Exception, err:
    print err
else:
    print '%s -> %s' % (library.name, output_selected)