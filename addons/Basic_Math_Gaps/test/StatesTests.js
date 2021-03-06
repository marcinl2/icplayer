TestCase("States Tests", {
    setUp: function () {
        this.presenter = AddonBasic_Math_Gaps_create();
        this.presenter.configuration = {
            'isVisible' : true
        };
    },

    'test getState works properly' : function() {
        this.presenter.$view = $(
            '<div class="basic-math-gaps-wrapper">' +
                '<div class="basic-math-gaps-container">' +
                    '<input value="1" />' +
                    '<span class="element">+</span>' +
                    '<input value="2" />' +
                    '<span class="element">=</span>' +
                    '<span class="element">3</span>' +
                '</div>' +
            '</div>');

        var stateString = this.presenter.getState();

        assertEquals('{\"values\":[\"1\",\"2\"],\"isVisible\":true}', stateString);
    },

    'test setState works properly' : function() {
        this.presenter.$view = $(
            '<div class="basic-math-gaps-wrapper">' +
                '<div class="basic-math-gaps-container">' +
                '<input value="" />' +
                '<span class="element">+</span>' +
                '<input value="" />' +
                '<span class="element">=</span>' +
                '<span class="element">3</span>' +
                '</div>' +
                '</div>');

        this.presenter.setState('{\"values\":[\"1\",\"2\"]}');

        assertEquals('1', this.presenter.$view.find('input:eq(0)').val());
        assertEquals('2', this.presenter.$view.find('input:eq(1)').val());
    }
});