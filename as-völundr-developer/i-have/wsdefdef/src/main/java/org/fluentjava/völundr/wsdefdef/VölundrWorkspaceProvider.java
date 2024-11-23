package org.fluentjava.völundr.wsdefdef;

import org.fluentjava.iwant.api.javamodules.JavaSrcModule;
import org.fluentjava.iwant.api.wsdef.WorkspaceModuleContext;
import org.fluentjava.iwant.api.wsdef.WorkspaceModuleProvider;

public class VölundrWorkspaceProvider implements WorkspaceModuleProvider {

	@Override
	public JavaSrcModule workspaceModule(WorkspaceModuleContext ctx) {
		return JavaSrcModule.with().name("völundr-wsdef")
				.locationUnderWsRoot("as-völundr-developer/i-have/wsdef")
				.mainJava("src/main/java").mainDeps(ctx.iwantApiModules())
				.mainDeps(ctx.iwantPlugin().jacoco().withDependencies())
				.mainDeps(ctx.wsdefdefModule()).end();
	}

	@Override
	public String workspaceFactoryClassname() {
		return "org.fluentjava.völundr.wsdef.VölundrWorkspaceFactory";
	}

}
