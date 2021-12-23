// Generated from /home/diego/IdeaProjects/SimpleKnnRecommender/src/simpleknn/config/ConfigFile.g4 by ANTLR 4.7
package simpleknn.config;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ConfigFileParser}.
 */
public interface ConfigFileListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ConfigFileParser#start}.
	 * @param ctx the parse tree
	 */
	void enterStart(ConfigFileParser.StartContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConfigFileParser#start}.
	 * @param ctx the parse tree
	 */
	void exitStart(ConfigFileParser.StartContext ctx);
	/**
	 * Enter a parse tree produced by {@link ConfigFileParser#lines}.
	 * @param ctx the parse tree
	 */
	void enterLines(ConfigFileParser.LinesContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConfigFileParser#lines}.
	 * @param ctx the parse tree
	 */
	void exitLines(ConfigFileParser.LinesContext ctx);
	/**
	 * Enter a parse tree produced by {@link ConfigFileParser#line}.
	 * @param ctx the parse tree
	 */
	void enterLine(ConfigFileParser.LineContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConfigFileParser#line}.
	 * @param ctx the parse tree
	 */
	void exitLine(ConfigFileParser.LineContext ctx);
}