package org.eclipse.xtend.core.macro.fsaccess

import java.io.File
import java.net.URI
import org.eclipse.xtend.lib.macro.services.FolderHandle
import org.eclipse.xtext.parser.IEncodingProvider

class RuntimeFolderHandle extends RuntimeResourceHandle implements FolderHandle {

	new(File file, IEncodingProvider encodingProvider) {
		super(file, encodingProvider)
	}

	override getFile(String path) {
		val file = new File(path.fullPath)
		if (file.directory) {
			val message = '''Given path is an existed folder (not a file): '�file.canonicalPath�'.'''
			throw new IllegalStateException(message)
		}
		new RuntimeFileHandle(file, encodingProvider)
	}

	override getFolder(String path) {
		val folder = new File(path.fullPath)
		if (folder.file) {
			val message = '''Given path is an existed file (not a folder): '�folder.canonicalPath�'.'''
			throw new IllegalStateException(message)
		}
		new RuntimeFolderHandle(folder, encodingProvider)
	}

	def getFullPath(String path) {
		new URI('''�getPath�/�path�''').normalize.path
	}

}
