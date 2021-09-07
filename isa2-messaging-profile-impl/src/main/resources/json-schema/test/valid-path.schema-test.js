const fs = require("fs");
const JsonSchema = require("@hyperjump/json-schema");

describe('Testing info.x-edel-lifecycle ', () => {
    let schema;

    beforeEach( async () => {
        JsonSchema.setShouldMetaValidate(true);
        schema = await JsonSchema.get("file://./schemas/messaging-schema.json");
    });

    it('Valid path: /messaging/{service}/{action}/{messageId}', async () => {
        let data = JSON.parse(fs.readFileSync('./examples/messaging-path-submission.json'));
        const output = await JsonSchema.validate(schema, data, JsonSchema.BASIC);

        // console.log(JSON.stringify(output, null, "  "));
        expect(output).toBeDefined();
        expect(output.valid).toBe(true);
        expect(output.errors.length).toBe(0);

    });

    it('Valid path: /messaging/{service}/{action}/{messageId}/sync', async () => {
        let data = JSON.parse(fs.readFileSync('./examples/messaging-path-submission-sync.json'));
        const output = await JsonSchema.validate(schema, data, JsonSchema.BASIC);

        // console.log(JSON.stringify(output, null, "  "));
        expect(output).toBeDefined();
        expect(output.valid).toBe(true);
        expect(output.errors.length).toBe(0);

    });

    it('Valid path: /messaging/[service]/[action]/{messageId}/response', async () => {
        let data = JSON.parse(fs.readFileSync('./examples/messaging-path-response.json'));
        const output = await JsonSchema.validate(schema, data, JsonSchema.BASIC);

        // console.log(JSON.stringify(output, null, "  "));
        expect(output).toBeDefined();
        expect(output.valid).toBe(true);
        expect(output.errors.length).toBe(0);

    });

    it('Valid path: /messaging', async () => {
        let data = JSON.parse(fs.readFileSync('./examples/messaging-path-list-all.json'));
        const output = await JsonSchema.validate(schema, data, JsonSchema.BASIC);

        // console.log(JSON.stringify(output, null, "  "));
        expect(output).toBeDefined();
        expect(output.valid).toBe(true);
        expect(output.errors.length).toBe(0);

    });

    it('Valid path: /messaging/[service]', async () => {
        let data = JSON.parse(fs.readFileSync('./examples/messaging-path-list-service.json'));
        const output = await JsonSchema.validate(schema, data, JsonSchema.BASIC);

        // console.log(JSON.stringify(output, null, "  "));
        expect(output).toBeDefined();
        expect(output.valid).toBe(true);
        expect(output.errors.length).toBe(0);

    });
    it('Valid path: /messaging/[service]/[action]', async () => {
        let data = JSON.parse(fs.readFileSync('./examples/messaging-path-list-service-action.json'));
        const output = await JsonSchema.validate(schema, data, JsonSchema.BASIC);

        // console.log(JSON.stringify(output, null, "  "));
        expect(output).toBeDefined();
        expect(output.valid).toBe(true);
        expect(output.errors.length).toBe(0);

    });

    it('Valid path: /messaging/[service]/[action]/{messageId}/response', async () => {
        let data = JSON.parse(fs.readFileSync('./examples/messaging-path-list-response-all.json'));
        const output = await JsonSchema.validate(schema, data, JsonSchema.BASIC);

        // console.log(JSON.stringify(output, null, "  "));
        expect(output).toBeDefined();
        expect(output.valid).toBe(true);
        expect(output.errors.length).toBe(0);

    });

    it('Valid path: /messaging/[service]/[action]/{messageId}/response/[rService]', async () => {
        let data = JSON.parse(fs.readFileSync('./examples/messaging-path-list-response-service.json'));
        const output = await JsonSchema.validate(schema, data, JsonSchema.BASIC);

        // console.log(JSON.stringify(output, null, "  "));
        expect(output).toBeDefined();
        expect(output.valid).toBe(true);
        expect(output.errors.length).toBe(0);

    });

    it('Valid path: /messaging/[service]/[action]/{messageId}/response/[rService]/[rAction]', async () => {
        let data = JSON.parse(fs.readFileSync('./examples/messaging-path-list-response-service-action.json'));
        const output = await JsonSchema.validate(schema, data, JsonSchema.BASIC);

        // console.log(JSON.stringify(output, null, "  "));
        expect(output).toBeDefined();
        expect(output.valid).toBe(true);
        expect(output.errors.length).toBe(0);

    });

    it('Valid path: /messaging/[service]/[action]/{messageId}/response/[rService]/[rAction]/{messageId}', async () => {
        let data = JSON.parse(fs.readFileSync('./examples/messaging-path-get-response.json'));
        const output = await JsonSchema.validate(schema, data, JsonSchema.BASIC);

        // console.log(JSON.stringify(output, null, "  "));
        expect(output).toBeDefined();
        expect(output.valid).toBe(true);
        expect(output.errors.length).toBe(0);

    });
    it('Valid path: /messaging-webhook/{messageId}/response/[service]/[action]/{rMessageId}', async () => {
        let data = JSON.parse(fs.readFileSync('./examples/messaging-path-webhook-message.json'));
        const output = await JsonSchema.validate(schema, data, JsonSchema.BASIC);

        // console.log(JSON.stringify(output, null, "  "));
        expect(output).toBeDefined();
        expect(output.valid).toBe(true);
        expect(output.errors.length).toBe(0);

    });

    it('Valid path: /messaging-webhook/{messageId}/response/signal', async () => {
        let data = JSON.parse(fs.readFileSync('./examples/messaging-path-webhook-signal.json'));
        const output = await JsonSchema.validate(schema, data, JsonSchema.BASIC);

        // console.log(JSON.stringify(output, null, "  "));
        expect(output).toBeDefined();
        expect(output.valid).toBe(true);
        expect(output.errors.length).toBe(0);

    });
});