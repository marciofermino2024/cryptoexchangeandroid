package br.com.cryptoexchange.domain.usecase;

import br.com.cryptoexchange.domain.repository.ExchangeRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class GetExchangeListUseCase_Factory implements Factory<GetExchangeListUseCase> {
  private final Provider<ExchangeRepository> repositoryProvider;

  public GetExchangeListUseCase_Factory(Provider<ExchangeRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetExchangeListUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetExchangeListUseCase_Factory create(
      Provider<ExchangeRepository> repositoryProvider) {
    return new GetExchangeListUseCase_Factory(repositoryProvider);
  }

  public static GetExchangeListUseCase newInstance(ExchangeRepository repository) {
    return new GetExchangeListUseCase(repository);
  }
}
