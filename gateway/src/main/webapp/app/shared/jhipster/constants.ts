export const MESSAGE_ALERT_HEADER_NAME = 'x-gatewayapp-alert';
export const MESSAGE_ERROR_HEADER_NAME = 'x-gatewayapp-error';
export const MESSAGE_PARAM_HEADER_NAME = 'x-gatewayapp-params';
export const CSRF_TOKEN_HEADER_NAME = 'X-XSRF-TOKEN';
export const CSRF_TOKEN_COOKIE_NAME = 'XSRF-TOKEN';

export enum Authority {
  ADMIN = 'ROLE_ADMIN',
  USER = 'ROLE_USER',
}
