var assert = require('chai').assert;
var sinon = require('sinon');

let PromiseStore = require('../../build').PromiseStore;

describe('PromiseStore', function () {
  describe('.make()', function () {
    it("should track a promise's callbacks", function () {
      var promises = new PromiseStore();
      // There should be no tracked promises.
      assert.lengthOf(promises, 0);

      // Add one promise.
      new Promise(function (resolve, reject) {
        promises.make(randomToken(), resolve, reject);
      });
      assert.lengthOf(promises, 1);
    });

    // TODO: test existing token
  });

  describe('.keep()', function () {
    it('should call the resolve() function', function () {
      var promises = new PromiseStore();
      var token = randomToken();
      var promise = new Promise(function (resolve, reject) {
        promises.make(token, resolve, reject);
      });

      // Resolve the promise asynchronously.
      var randomPayload = randomToken();
      doLater(function () {
        assert.lengthOf(promises, 1);
        // TODO: this API probably should not return a resolve function;
        // it should call it immediately.
        promises.keep(token)(randomPayload);
      });

      return promise.then(function (actual) {
        assert.strictEqual(actual, randomPayload);
        assert.lengthOf(promises, 0);
      });
    });
  });

  function randomToken() {
    var range =  Number.MAX_SAFE_INTEGER - Number.MIN_SAFE_INTEGER;
    return Math.random() * range + Number.MIN_SAFE_INTEGER;
  }

  // Do something asynchronously.
  function doLater(callback) {
    return setTimeout(callback, 0);
  }
});
