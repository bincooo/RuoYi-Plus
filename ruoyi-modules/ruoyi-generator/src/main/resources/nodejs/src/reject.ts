import { observeAttributes, registerRejects } from '@bingco/react-reject/directive'

function extractComponentMeta(value) {
    const [,component, id] = value.split(':');
    return {
        id, component
    }
}

observeAttributes(document.body, (element) => {
    if (element.classList.contains('ant-drawer-content-wrapper')) {
        const proDrawer = element.querySelector('.ant-drawer-body > form');
        const dom = proDrawer.closest('.ant-drawer-section');
        const run = (observer?: boolean) => {
            const dnd = proDrawer.getAttribute('data-dnd');
            if (dnd) {
                dom.setAttribute('data-dnd', dnd);
                proDrawer.removeAttribute('data-dnd');
                if (observer) {
                    observeAttributes(proDrawer, () => { run() }, { subtree: false, attributeFilter: ['data-dnd'] });
                }
            }
        }
        run(true);
        return;
    }
});

registerRejects<any>(/ProForm\S+/, (value, props) => {
    const fieldProps = props.fieldProps ?? {};
    props.fieldProps = {
        ...fieldProps,
        'data-dnd': value,
    };
});
