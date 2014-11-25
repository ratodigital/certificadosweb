package util


class Script {
		
	static def String evalScript(Map vars, String script) {
	  def binding = new Binding()
	  vars.entrySet().each { var ->
		binding.setVariable(var.key, var.value);
	  }
	  def shell = new GroovyShell(binding) 
		def groovyScript = """
	${script.trim()}
	"""
	  shell.evaluate("return \"\"\"$groovyScript\"\"\"")
	}
}
