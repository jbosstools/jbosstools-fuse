package org.fusesource.ide.fabric.navigator;

import java.util.Collections;
import java.util.List;

import org.fusesource.ide.commons.tree.Node;
import org.fusesource.ide.fabric.actions.ProfilesContentProvider;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ProfileParentsContentProvider extends ProfilesContentProvider {
	private final ProfileNode ignoreProfileNode;

	public ProfileParentsContentProvider(ProfileNode node) {
		this.ignoreProfileNode = node;
	}

	@Override
	protected List<Node> getChildrenList(final Node node) {
		if (node == null || node == this.ignoreProfileNode) {
			return Collections.EMPTY_LIST;
		}
		return Lists.newArrayList(Iterables.filter(node.getChildrenList(), new Predicate<Node>() {

			@Override
			public boolean apply(Node that) {
				return ignoreProfileNode != that;
			}
		}));
	}



}
