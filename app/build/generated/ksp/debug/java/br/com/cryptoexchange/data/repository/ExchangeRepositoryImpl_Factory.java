package br.com.cryptoexchange.data.repository;

import br.com.cryptoexchange.data.CMCLogger;
import br.com.cryptoexchange.data.api.CmcApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class ExchangeRepositoryImpl_Factory implements Factory<ExchangeRepositoryImpl> {
  private final Provider<CmcApiService> apiProvider;

  private final Provider<CMCLogger> loggerProvider;

  public ExchangeRepositoryImpl_Factory(Provider<CmcApiService> apiProvider,
      Provider<CMCLogger> loggerProvider) {
    this.apiProvider = apiProvider;
    this.loggerProvider = loggerProvider;
  }

  @Override
  public ExchangeRepositoryImpl get() {
    return newInstance(apiProvider.get(), loggerProvider.get());
  }

  public static ExchangeRepositoryImpl_Factory create(Provider<CmcApiService> apiProvider,
      Provider<CMCLogger> loggerProvider) {
    return new ExchangeRepositoryImpl_Factory(apiProvider, loggerProvider);
  }

  public static ExchangeRepositoryImpl newInstance(CmcApiService api, CMCLogger logger) {
    return new ExchangeRepositoryImpl(api, logger);
  }
}
