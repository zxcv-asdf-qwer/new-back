package co.kr.compig.global.interceptor;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import co.kr.compig.global.dto.pagination.PagingRequest;
import co.kr.compig.global.dto.pagination.PagingResult;

@Intercepts({
	@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
		RowBounds.class, ResultHandler.class})})
@SuppressWarnings("rawtypes")
public class PagingInterceptor implements Interceptor {

	static int MAPPED_STATEMENT_INDEX = 0;
	static int PARAMETER_INDEX = 1;

	static String PAGING_PRE = "\nSELECT COUNT(*) OVER() AS totalCount, ROW_NUMBER() OVER (%s) AS rowNum, AA.* FROM (\n\n\t"; // ORDER BY
	static String PAGING_POST = "\n\n) AA %s %s"; // ORDER BY, LIMIT OFFSET

	public Object intercept(final Invocation invocation) throws Throwable {
		final Object[] queryArgs = invocation.getArgs();
		final MappedStatement ms = (MappedStatement)queryArgs[MAPPED_STATEMENT_INDEX];
		if (!PagingResult.class.isAssignableFrom(ms.getResultMaps().get(0).getType())) {
			return invocation.proceed();
		}
		final Object parameter = queryArgs[PARAMETER_INDEX];

		final BoundSql boundSql = ms.getBoundSql(parameter);
		BoundSql newBoundSql = copyFromBoundSql(ms, boundSql,
			getPage(boundSql.getSql().trim(), parameter));

		MappedStatement newMs = copyFromMappedStatement(ms, new BoundSqlSqlSource(newBoundSql));

		queryArgs[MAPPED_STATEMENT_INDEX] = newMs;

		return invocation.proceed();
	}

	public String convertCamelToSnake(String input) {
		// 결과를 저장할 StringBuilder 객체 생성
		StringBuilder result = new StringBuilder();

		// 입력 문자열을 공백으로 나누기
		String[] words = input.split(" ");

		for (String word : words) {
			StringBuilder convertedWord = new StringBuilder();
			for (int i = 0; i < word.length(); i++) {
				char ch = word.charAt(i);
				// 대문자를 만나면 앞에 _를 추가하고 소문자로 변환
				if (Character.isUpperCase(ch)) {
					if (i > 0)
						convertedWord.append('_');
					convertedWord.append(Character.toLowerCase(ch));
				} else {
					convertedWord.append(ch);
				}
			}
			// 변환된 단어를 결과에 추가
			result.append(convertedWord).append(" ");
		}

		// 마지막 공백 제거
		return result.toString().trim();
	}

	private String getPage(String org, Object parameter) {
		if (parameter instanceof PagingRequest pagingDto) {
			StringBuilder sortStr = new StringBuilder();
			String pagingStr = "";
			if (pagingDto.getSort() != null) {
				sortStr = new StringBuilder("ORDER BY ");
				for (String sort : pagingDto.getSort()) {
					sortStr.append(sort).append(",");
				}
				sortStr = new StringBuilder(sortStr.substring(0, sortStr.length() - 1));
			}

			if (isPaging(pagingDto)) {
				int limit = pagingDto.getSize();
				if (pagingDto.getPage() == 0) {
					pagingDto.setPage(1);
				}
				int offset = (pagingDto.getPage() * pagingDto.getSize()) - pagingDto.getSize();

				pagingStr = " LIMIT " + limit + " OFFSET " + offset;
				return String.format(PAGING_PRE, sortStr, pagingDto.getPage(), pagingDto.getSize()) + org
					+ String.format(PAGING_POST, sortStr, pagingStr);
			}
			return org;
		} else {
			return org;
		}
	}

	private boolean isPaging(PagingRequest pagingDto) {
		return pagingDto.getPage() != null && pagingDto.getSize() != null;
	}

	// see: MapperBuilderAssistant
	private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
		MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(),
			newSqlSource, ms.getSqlCommandType());

		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
			StringBuilder keyProperties = new StringBuilder();
			for (String keyProperty : ms.getKeyProperties()) {
				keyProperties.append(keyProperty).append(",");
			}
			keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
			builder.keyProperty(keyProperties.toString());
		}

		builder.timeout(ms.getTimeout());

		builder.parameterMap(ms.getParameterMap());

		builder.resultMaps(ms.getResultMaps());
		builder.resultSetType(ms.getResultSetType());

		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());
		builder.useCache(ms.isUseCache());

		return builder.build();
	}

	public static BoundSql copyFromBoundSql(MappedStatement ms, BoundSql boundSql, String sql) {
		BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, boundSql.getParameterMappings(),
			boundSql.getParameterObject());
		for (ParameterMapping mapping : boundSql.getParameterMappings()) {
			String prop = mapping.getProperty();
			if (boundSql.hasAdditionalParameter(prop)) {
				newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
			}
		}
		return newBoundSql;
	}

	public static class BoundSqlSqlSource implements SqlSource {

		BoundSql boundSql;

		public BoundSqlSqlSource(BoundSql boundSql) {
			this.boundSql = boundSql;
		}

		public BoundSql getBoundSql(Object parameterObject) {
			return boundSql;
		}
	}
}
