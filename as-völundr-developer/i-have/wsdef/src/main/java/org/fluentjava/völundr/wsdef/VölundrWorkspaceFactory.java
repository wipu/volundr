package org.fluentjava.völundr.wsdef;

import org.fluentjava.iwant.api.wsdef.Workspace;
import org.fluentjava.iwant.api.wsdef.WorkspaceContext;
import org.fluentjava.iwant.api.wsdef.WorkspaceFactory;

public class VölundrWorkspaceFactory implements WorkspaceFactory {

	@Override
	public Workspace workspace(WorkspaceContext ctx) {
		return new VölundrWorkspace(ctx);
	}

}
