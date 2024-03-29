export function isPackageInstalledAsync(packageName: string): Promise<boolean>;

export function isRunningServiceAsync(serviceName: string): Promise<boolean>;

export function startAsync<TResult>(
  packageName: string,
  className: string,
  action: string,
  params: object | null
): Promise<TResult>;
