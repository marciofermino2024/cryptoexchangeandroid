package br.com.cryptoexchange.data;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class CMCLogger_Factory implements Factory<CMCLogger> {
  @Override
  public CMCLogger get() {
    return newInstance();
  }

  public static CMCLogger_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CMCLogger newInstance() {
    return new CMCLogger();
  }

  private static final class InstanceHolder {
    private static final CMCLogger_Factory INSTANCE = new CMCLogger_Factory();
  }
}
