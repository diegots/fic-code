// Generated from /home/diego/IdeaProjects/SimpleKnnRecommender/src/simpleknn/config/ConfigFile.g4 by ANTLR 4.7
package simpleknn.config;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides an empty implementation of {@link ConfigFileListener},
 * which can be extended to create a listener which only needs to handle a subset
 * of the available methods.
 */
public class ConfigFileBaseListener implements ConfigFileListener {
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterStart(ConfigFileParser.StartContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitStart(ConfigFileParser.StartContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterLines(ConfigFileParser.LinesContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitLines(ConfigFileParser.LinesContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterLine(ConfigFileParser.LineContext ctx) {
		Map<String, String> m = new HashMap<>();

//		if (ctx.Key() != null && ctx.Value() != null)
//			System.err.println("-> " + ctx.Key().getText() + " - " + ctx.Value());

		if (ctx.Key() != null && ctx.Value() != null)
			m.put(ctx.Key().getText(), ctx.Value().getText());

		// Save data
		ConfigFile.getResMap().putAll(m);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitLine(ConfigFileParser.LineContext ctx) { }

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void enterEveryRule(ParserRuleContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void exitEveryRule(ParserRuleContext ctx) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void visitTerminal(TerminalNode node) { }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation does nothing.</p>
	 */
	@Override public void visitErrorNode(ErrorNode node) { }
}