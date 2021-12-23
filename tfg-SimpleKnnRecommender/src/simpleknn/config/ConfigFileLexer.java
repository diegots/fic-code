// Generated from /home/diego/IdeaProjects/SimpleKnnRecommender/src/simpleknn/config/ConfigFile.g4 by ANTLR 4.7
package simpleknn.config;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ConfigFileLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, Key=2, Value=3, Comment=4, WS=5;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "Key", "Value", "Comment", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'='"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, "Key", "Value", "Comment", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public ConfigFileLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "ConfigFile.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\7\u0085\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\5\3q\n\3\3\4\6\4t\n\4\r\4\16\4u\3\5\3\5\7\5z\n\5\f\5\16\5}"+
		"\13\5\3\6\6\6\u0080\n\6\r\6\16\6\u0081\3\6\3\6\2\2\7\3\3\5\4\7\5\t\6\13"+
		"\7\3\2\5\5\2\60<C\\c|\4\2\13\f\17\17\5\2\13\f\17\17\"\"\2\u008f\2\3\3"+
		"\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\3\r\3\2\2\2\5"+
		"p\3\2\2\2\7s\3\2\2\2\tw\3\2\2\2\13\177\3\2\2\2\r\16\7?\2\2\16\4\3\2\2"+
		"\2\17\20\7F\2\2\20\21\7C\2\2\21\22\7V\2\2\22\23\7C\2\2\23\24\7U\2\2\24"+
		"\25\7G\2\2\25\26\7V\2\2\26\27\7a\2\2\27\30\7R\2\2\30\31\7C\2\2\31\32\7"+
		"V\2\2\32q\7J\2\2\33\34\7F\2\2\34\35\7D\2\2\35\36\7a\2\2\36\37\7E\2\2\37"+
		" \7Q\2\2 !\7P\2\2!\"\7P\2\2\"#\7G\2\2#$\7E\2\2$%\7V\2\2%&\7K\2\2&\'\7"+
		"Q\2\2\'(\7P\2\2()\7a\2\2)*\7U\2\2*+\7V\2\2+,\7T\2\2,-\7K\2\2-.\7P\2\2"+
		".q\7I\2\2/\60\7F\2\2\60\61\7D\2\2\61\62\7a\2\2\62\63\7G\2\2\63\64\7P\2"+
		"\2\64\65\7F\2\2\65\66\7R\2\2\66\67\7Q\2\2\678\7K\2\289\7P\2\29q\7V\2\2"+
		":;\7F\2\2;<\7D\2\2<=\7a\2\2=>\7R\2\2>?\7C\2\2?@\7U\2\2@A\7U\2\2AB\7Y\2"+
		"\2Bq\7F\2\2CD\7F\2\2DE\7D\2\2EF\7a\2\2FG\7V\2\2GH\7[\2\2HI\7R\2\2Iq\7"+
		"G\2\2JK\7F\2\2KL\7D\2\2LM\7a\2\2MN\7W\2\2NO\7U\2\2OP\7G\2\2Pq\7T\2\2Q"+
		"R\7P\2\2RS\7G\2\2ST\7K\2\2TU\7I\2\2UV\7J\2\2VW\7D\2\2WX\7Q\2\2XY\7T\2"+
		"\2YZ\7a\2\2Z[\7U\2\2[\\\7K\2\2\\]\7\\\2\2]q\7G\2\2^_\7P\2\2_`\7W\2\2`"+
		"a\7O\2\2ab\7D\2\2bc\7G\2\2cd\7T\2\2de\7a\2\2ef\7T\2\2fg\7G\2\2gh\7E\2"+
		"\2hq\7U\2\2ij\7W\2\2jk\7U\2\2kl\7G\2\2lm\7T\2\2mn\7a\2\2no\7K\2\2oq\7"+
		"F\2\2p\17\3\2\2\2p\33\3\2\2\2p/\3\2\2\2p:\3\2\2\2pC\3\2\2\2pJ\3\2\2\2"+
		"pQ\3\2\2\2p^\3\2\2\2pi\3\2\2\2q\6\3\2\2\2rt\t\2\2\2sr\3\2\2\2tu\3\2\2"+
		"\2us\3\2\2\2uv\3\2\2\2v\b\3\2\2\2w{\7%\2\2xz\n\3\2\2yx\3\2\2\2z}\3\2\2"+
		"\2{y\3\2\2\2{|\3\2\2\2|\n\3\2\2\2}{\3\2\2\2~\u0080\t\4\2\2\177~\3\2\2"+
		"\2\u0080\u0081\3\2\2\2\u0081\177\3\2\2\2\u0081\u0082\3\2\2\2\u0082\u0083"+
		"\3\2\2\2\u0083\u0084\b\6\2\2\u0084\f\3\2\2\2\7\2pu{\u0081\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}