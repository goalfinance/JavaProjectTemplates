/**
 * 
 */
package com.goalfinance.projecttemplates.test

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.goalfinance.projecttemplates.ProjectTemplate;

/**
 * @author panqr
 *
 */
class ProjectTemplateTest {
	private static final String PROJECT_NAME = "Some project"
	
	@Rule public TemporaryFolder rootFolder = new TemporaryFolder()
	
	@Test void 'fromRoot(String): empty'(){
		assert !(new File( rootFolder.root, PROJECT_NAME ).exists())

		ProjectTemplate.fromRoot( "${rootFolder.root}/$PROJECT_NAME" )

		assert new File( rootFolder.root, PROJECT_NAME ).exists()
	}
	
	@Test void 'fromRoot(String) : with content'(){
		assert !(new File(rootFolder.root, PROJECT_NAME).exists())
		
		File templateFile = rootFolder.newFile('template.tmpl')
		templateFile.text = 'The answer is ${bar}'
		
		ProjectTemplate.fromRoot("${rootFolder.root}/$PROJECT_NAME") { 
			'stuff' {
				'README.txt' '''
					This is a generated README file.
				'''
				'deeper' {}
				'foo' template:templateFile.toString(), bar:42
			}
		}
		
		assert new File( rootFolder.root, PROJECT_NAME ).exists()
		assert new File( rootFolder.root, "$PROJECT_NAME/stuff" ).exists()
		assert new File( rootFolder.root, "$PROJECT_NAME/stuff/deeper" ).exists()

		assertFileText rootFolder.root, "$PROJECT_NAME/stuff/README.txt", 'This is a generated README file.'
		assertFileText rootFolder.root, "$PROJECT_NAME/stuff/foo", 'The answer is 42'
	}
	
	private void assertFileText( File dir, String path, String expectedText ){
		def targetFile = new File( dir, path )
		assert targetFile.exists()
		assert targetFile.text.trim() == expectedText
	}
	
}
