// ***********************************************************
// This example plugins/index.js can be used to load plugins
//
// You can change the location of this file or turn off loading
// the plugins file with the 'pluginsFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/plugins-guide
// ***********************************************************

// This function is called when a project is opened or re-opened (e.g. due to
// the project's config changing)
import installLogsPrinter from 'cypress-terminal-report/src/installLogsPrinter';

export default (on: Cypress.PluginEvents, config: Cypress.PluginConfigOptions) => {
  installLogsPrinter(on);
  on('before:browser:launch', (browser, launchOptions) => {
    if (browser.name === 'chrome' && browser.isHeadless) {
      launchOptions.args.push('--disable-gpu', '--no-sandbox', '--disable-dev-shm-usage');
    }
    if (browser.family === 'chromium' && browser.name !== 'electron') {
      launchOptions.args.push(
        // Forces Chrome/Edge to open a large window so the viewport doesn't scale down
        '--window-size=1920,1080',
      );
    }
    return launchOptions;
  });

  // Allows logging with cy.task('log', 'message') or cy.task('table', object)
  on('task', {
    log(message) {
      console.log(message);
      return null;
    },
    table(message) {
      console.table(message);
      return null;
    },
  });

  return config;
};
