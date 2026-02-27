package br.com.cryptoexchange.di;

import br.com.cryptoexchange.data.api.CmcApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

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
public final class NetworkModule_ProvideCmcApiServiceFactory implements Factory<CmcApiService> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideCmcApiServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public CmcApiService get() {
    return provideCmcApiService(retrofitProvider.get());
  }

  public static NetworkModule_ProvideCmcApiServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideCmcApiServiceFactory(retrofitProvider);
  }

  public static CmcApiService provideCmcApiService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideCmcApiService(retrofit));
  }
}
